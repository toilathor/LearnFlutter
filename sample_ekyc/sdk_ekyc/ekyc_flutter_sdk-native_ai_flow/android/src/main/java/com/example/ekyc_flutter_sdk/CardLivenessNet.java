package com.example.ekyc_flutter_sdk;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.SystemClock;
import android.os.Trace;
import android.util.Log;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;

import org.tensorflow.lite.nnapi.NnApiDelegate;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeOp.ResizeMethod;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import org.tensorflow.lite.support.common.ops.NormalizeOp;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class CardLivenessNet extends Classifier {

    /**
     * The quantized model does not require normalization, thus set mean as 0.0f,
     * and std as 1.0f to
     * bypass the normalization.
     */
    private static final float IMAGE_MEAN = 0.0f;

    private static final float IMAGE_STD = 1.0f;

    /**
     * Quantized MobileNet requires additional dequantization to the output
     * probability.
     */
    private static final float PROBABILITY_MEAN = 0.0f;

    private static final float PROBABILITY_STD = 255.0f;

    public static int OUTPUT_SIZE = 41;

    public static final String CONTENT_TYPE_CARD_FONT = "CARD_FONT";
    public static final String CONTENT_TYPE_CARD_BACK = "CARD_BACK";

    private enum RecogStatus {
        invalid_background,
        // Valid
        valid_front_CCCD,
        valid_back_CCCD,
        valid_front_CMND,
        valid_back_CMND,

        // Invalid
        // CCCD
        invalid_hand_cover_CCCD_front,
        invalid_hand_cover_CCCD_back,
        invalid_insert_text_paper_CCCD_front,
        invalid_insert_text_paper_CCCD_back,
        invalid_icon_CCCD_front,
        invalid_icon_CCCD_back,
        invalid_device_CCCD,
        // invalid_fake_profile_image_CCCD,
        invalid_cut_edge_CCCD_front,
        invalid_cut_edge_CCCD_back,
        // invalid_blank_template_CCCD_front,
        // invalid_blank_template_CCCD_back,
        invalid_spotlight_CCCD_front,
        invalid_spotlight_CCCD_back,

        // CMND
        invalid_hand_cover_CMND_front,
        invalid_hand_cover_CMND_back,
        invalid_insert_text_paper_CMND_front,
        invalid_insert_text_paper_CMND_back,
        invalid_icon_CMND_front,
        invalid_icon_CMND_back,
        invalid_device_CMND,
        // invalid_fake_profile_image_CMND,
        invalid_cut_edge_CMND_front,
        invalid_cut_edge_CMND_back,
        // invalid_blank_template_CMND_front,
        // invalid_blank_template_CMND_back,
        invalid_spotlight_CMND_front,
        invalid_spotlight_CMND_back,

        // both CMND, CCCD
        // invalid_fake_photocopy,
        // invalid_photo_color_both,

        valid_front_chip,
        valid_back_chip,
        invalid_spotlight_CHIP_front,
        invalid_spotlight_CHIP_back,
        invalid_insert_text_paper_CHIP_front,
        invalid_insert_text_paper_CHIP_back,
        invalid_icon_CHIP_front,
        invalid_icon_CHIP_back,
        invalid_hand_cover_CHIP_front,
        invalid_hand_cover_CHIP_back,
        invalid_device_CHIP,
        invalid_cut_edge_CHIP_front,
        invalid_cut_edge_CHIP_back,

        // PASSPORT
        valid_PASSPORT,

        // step
        invalid_side,
    }

    /**
     * Initializes a {@code ClassifierQuantizedMobileNet}.
     *
     * @param activity
     */
    public CardLivenessNet(MappedByteBuffer buffer, Device device, int numThreads)
            throws IOException {
        super(buffer, device, numThreads);
    }

    @Override
    protected TensorImage loadImage(Bitmap bitmap) {
        inputImageBuffer.load(bitmap);
        ImageProcessor imageProcessor = new ImageProcessor.Builder()
                .add(new ResizeOp(210, 280, ResizeOp.ResizeMethod.BILINEAR))
                .build();
        return imageProcessor.process(inputImageBuffer);
    }

    @Override
    public List<Float> recognizeImage(Bitmap bitmap) {
        // Logs this method so that it can be analyzed with systrace.
        Trace.beginSection("recognizeImage");
        Trace.beginSection("loadImage");

        long startLoadImage = System.currentTimeMillis();
        inputImageBuffer = loadImage(bitmap);
        Trace.endSection();

        // Runs the inference call.
        Trace.beginSection("runInference");
        inputImageBuffer.getBuffer().rewind();

        // print the ByteBuffer
        System.out.println("Original ByteBuffer: ");
        long start = System.currentTimeMillis();
        tflite.run(inputImageBuffer.getBuffer(), super.outputProbabilityBuffer.getBuffer().rewind());

        Trace.endSection();
        List<Float> list = new ArrayList<>();

        try {
            outputProbabilityBuffer.getBuffer().rewind();
            for (int i = 0; i < OUTPUT_SIZE; i++) {
                float value = outputProbabilityBuffer.getBuffer().getFloat();
                list.add(value);
            }
        } catch (BufferUnderflowException ignored) {
        }
        return list;
    }

    @Override
    public List<Float> recognizeImageBatch(final Bitmap bitmap1, final Bitmap bitmap2) {
        Trace.beginSection("recognizeImage");
        Trace.beginSection("loadImage");
        // preprocess input
        int[] dimension = new int[4];
        dimension[0] = 2;
        dimension[1] = 210;
        dimension[2] = 280;
        dimension[3] = 3;
        ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
        bitmaps.add(bitmap1);
        bitmaps.add(bitmap2);
        float[][][][] inputImageFloat = loadImage4D(bitmaps, dimension);

        // prepare output
        int[] probabilityShape = tflite.getOutputTensor(0).shape();
        DataType probabilityDataType1 = tflite.getOutputTensor(0).dataType();
        int[] tmpOutputShape = new int[2];
        tmpOutputShape[0] = dimension[0];
        tmpOutputShape[1] = probabilityShape[1];
        OUTPUT_SIZE = probabilityShape[1];

        TensorBuffer outputProbabilityBuffer1 = TensorBuffer.createFixedSize(tmpOutputShape, probabilityDataType1);

        // run intepreter
        tflite.run(inputImageFloat, outputProbabilityBuffer1.getBuffer().rewind());
        Trace.endSection();

        // postprocess
        List<Float> list = new ArrayList<>();
        try {
            outputProbabilityBuffer1.getBuffer().rewind();
            for (int i = 0; i < tmpOutputShape[0] * tmpOutputShape[1]; i++) {
                float value = outputProbabilityBuffer1.getBuffer().getFloat();
                list.add(value);
            }
        } catch (BufferUnderflowException ignored) {
        }
        return list;
    }

    @Override
    protected float[][][][] loadImage4D(ArrayList<Bitmap> bitmaps, int[] dimension) {
        int batchNum = dimension[0];
        int height = dimension[1];
        int width = dimension[2];
        int channel = dimension[3];
        float[][][][] input = new float[batchNum][height][width][channel];
        for (int i = 0; i < batchNum; i++) {
            Bitmap tmpBitmap = bitmaps.get(i);
            tmpBitmap = Bitmap.createScaledBitmap(tmpBitmap, width, height, true);
            for (int h = 0; h < height; h++) {
                for (int w = 0; w < width; w++) {
                    int pixel = tmpBitmap.getPixel(w, h);
                    input[i][h][w][0] = Color.red(pixel);
                    input[i][h][w][1] = Color.green(pixel);
                    input[i][h][w][2] = Color.blue(pixel);
                }
            }
        }
        return input;
    }

    @Override
    protected String getModelPath() {
        // you can download this file from
        // see build.gradle for where to obtain this file. It should be auto
        // downloaded into assets.
        return "liveness.tflite";
        // return "mobilenet_dummy.tflite";
    }

    @Override
    protected String getLabelPath() {
        return "labels.txt";
    }

    @Override
    protected TensorOperator getPreprocessNormalizeOp() {
        return new NormalizeOp(IMAGE_MEAN, IMAGE_STD);
    }

    @Override
    protected TensorOperator getPostprocessNormalizeOp() {
        return new NormalizeOp(PROBABILITY_MEAN, PROBABILITY_STD);
    }

    public static int[] maxIndexPerBatch(List<Float> results) {
        // GET MAX INDEX FROM EACH IMAGE
        // results: LIST SCORE FROM MODEL
        if (results == null || results.size() == 0)
            return null;
        int batchSize = results.size() / OUTPUT_SIZE;
        int[] listMaxIndex = new int[batchSize];
        for (int i = 0; i < batchSize; i++) {
            float maxValue = results.get(i * OUTPUT_SIZE);
            int maxIndex = 0;
            for (int j = 0; j < OUTPUT_SIZE; j++) {
                if (results.get(i * OUTPUT_SIZE + j) > maxValue) {
                    maxValue = results.get(i * OUTPUT_SIZE + j);
                    maxIndex = j;
                }
            }
            listMaxIndex[i] = maxIndex;
        }
        return listMaxIndex;
    }

    private static int getMaxIndex(int[] index) {
        if (index.length == OUTPUT_SIZE) {
            return index[0];
        }
        // handle batch output
        else
            return index[0];
    }

    public static boolean isCardValid(List<Float> results, String type) {
        RecogStatus currentRecogStatus = RecogStatus.invalid_background;

        int[] maxResults = maxIndexPerBatch(results);

        int index = getMaxIndex(maxResults);
        currentRecogStatus = RecogStatus.values()[index];

        if (currentRecogStatus == RecogStatus.valid_PASSPORT) {
            return true;
        }

        if (type.equals(CONTENT_TYPE_CARD_FONT) && (currentRecogStatus == RecogStatus.valid_back_CCCD
                || currentRecogStatus == RecogStatus.valid_back_chip
                || currentRecogStatus == RecogStatus.valid_back_CMND)) {
            currentRecogStatus = RecogStatus.invalid_side;
            return false;
        }

        if (currentRecogStatus == RecogStatus.invalid_device_CMND
                || currentRecogStatus == RecogStatus.invalid_device_CCCD
                || currentRecogStatus == RecogStatus.invalid_device_CHIP) {
            if (results.get(index) < 0.6) {
                return true;
            }
        }

        if (currentRecogStatus == RecogStatus.invalid_insert_text_paper_CCCD_front
                || currentRecogStatus == RecogStatus.invalid_insert_text_paper_CCCD_back
                || currentRecogStatus == RecogStatus.invalid_insert_text_paper_CMND_front
                || currentRecogStatus == RecogStatus.invalid_insert_text_paper_CMND_back
                || currentRecogStatus == RecogStatus.invalid_insert_text_paper_CHIP_front
                || currentRecogStatus == RecogStatus.invalid_insert_text_paper_CHIP_back) {
            if (results.get(index) < 0.75) {
                return true;
            }
        }

        if (type.equals(CONTENT_TYPE_CARD_BACK) && (currentRecogStatus == RecogStatus.valid_front_CCCD
                || currentRecogStatus == RecogStatus.valid_front_chip
                || currentRecogStatus == RecogStatus.valid_front_CMND)) {
            currentRecogStatus = RecogStatus.invalid_side;
            return false;
        }
        if (type.equals(CONTENT_TYPE_CARD_FONT) && (currentRecogStatus == RecogStatus.valid_front_CCCD
                || currentRecogStatus == RecogStatus.valid_front_chip
                || currentRecogStatus == RecogStatus.valid_front_CMND)) {
            return true;
        } else if (type.equals(CONTENT_TYPE_CARD_BACK) && (currentRecogStatus == RecogStatus.valid_back_CCCD
                || currentRecogStatus == RecogStatus.valid_back_chip
                || currentRecogStatus == RecogStatus.valid_back_CMND)) {
            return true;
        }
        return false;
    }
}