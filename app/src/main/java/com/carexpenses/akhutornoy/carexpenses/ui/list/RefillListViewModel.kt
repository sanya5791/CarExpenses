package com.carexpenses.akhutornoy.carexpenses.ui.list

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.carexpenses.akhutornoy.carexpenses.base.BaseViewModel
import com.carexpenses.akhutornoy.carexpenses.domain.Refill
import com.carexpenses.akhutornoy.carexpenses.domain.RefillDao
import com.carexpenses.akhutornoy.carexpenses.ui.list.recyclerview.RefillItem
import com.carexpenses.akhutornoy.carexpenses.utils.applyProgressBar
import com.carexpenses.akhutornoy.carexpenses.utils.applySchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class RefillListViewModel(
        private val refillDao: RefillDao) : BaseViewModel() {

    private lateinit var onLoadRefillsLiveData: MutableLiveData<List<RefillItem>>
    private val dateFormatLong = SimpleDateFormat("dd-MM-yy HH:mm", Locale.getDefault())

    fun getRefills(fuelType: Refill.FuelType): LiveData<List<RefillItem>> {
        if (::onLoadRefillsLiveData.isInitialized) {
            return onLoadRefillsLiveData
        }

        onLoadRefillsLiveData = MutableLiveData()
        autoUnsubscribe(
                refillDao.getByFuelType(fuelType.value)
                        .map { refills ->  mapToRefillItems(refills)}
                        .subscribeOn(Schedulers.io())
                        .applySchedulers()
                        .applyProgressBar(this)
                        .subscribe(
                                { refills -> onLoadRefillsLiveData.value = refills},
                                this::showError
                        )
        )

        return onLoadRefillsLiveData
    }

    private fun mapToRefillItems(items: List<Refill>): List<RefillItem> {

        return items.map { dbItem ->
            val date = dateFormatLong.format(Date(dbItem.createdAt))
            RefillItem(
                    dbId = dbItem.createdAt,
                    consumption = -1,
                    date = date,
                    litersCount = dbItem.litersCount,
                    trafficMode = dbItem.trafficMode().name,
                    isNoteAvailable = dbItem.note != Refill.UNSET_STR
            )
        }
    }
}