package com.example.next_ble.ble

import android.os.ParcelUuid
import com.example.next_ble.*
import com.polidea.rxandroidble2.RxBleDeviceServices
import com.example.next_ble.model.ScanMode
import com.example.next_ble.utils.Duration
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import java.util.UUID

@Suppress("TooManyFunctions")
interface BleClient {

    val connectionUpdateSubject: BehaviorSubject<ConnectionUpdate>

    fun initializeClient()
    fun scanForDevices(
        services: List<ParcelUuid>,
        scanMode: ScanMode,
        requireLocationServicesEnabled: Boolean
    ): Observable<ScanInfo>

    fun connectToDevice(deviceId: String, timeout: Duration)
    fun disconnectDevice(deviceId: String)
    fun disconnectAllDevices()
    fun discoverServices(deviceId: String): Single<RxBleDeviceServices>
    fun clearGattCache(deviceId: String): Completable
    fun readCharacteristic(deviceId: String, characteristic: UUID): Single<CharOperationResult>
    fun setupNotification(deviceId: String, characteristic: UUID): Observable<ByteArray>
    fun writeCharacteristicWithResponse(
        deviceId: String,
        characteristic: UUID,
        value: ByteArray
    ): Single<CharOperationResult>

    fun writeCharacteristicWithoutResponse(
        deviceId: String,
        characteristic: UUID,
        value: ByteArray
    ): Single<CharOperationResult>

    fun negotiateMtuSize(deviceId: String, size: Int): Single<MtuNegotiateResult>
    fun observeBleStatus(): Observable<BleStatus>
    fun requestConnectionPriority(deviceId: String, priority: ConnectionPriority):
            Single<RequestConnectionPriorityResult>
}
