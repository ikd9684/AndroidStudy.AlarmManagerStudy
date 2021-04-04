package app.ikd9684.android.study.local_notification_study.activities_fragments

import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import app.ikd9684.android.study.local_notification_study.R
import app.ikd9684.android.study.local_notification_study.databinding.ActivityMainBinding
import app.ikd9684.android.study.local_notification_study.utils.misc.LocalNotificationUtil
import org.joda.time.DateTime


class MainActivity : AppCompatActivity() {

    companion object {
        private const val alarmName = "ReservationTime"
    }

    class NotificationViewModel : ViewModel() {

        companion object {

            fun getNow(plusMinutes: Int): DateTime {
                val now = DateTime().plusMinutes(plusMinutes)
                return DateTime(
                    now.year,
                    now.monthOfYear,
                    now.dayOfMonth,
                    now.hourOfDay,
                    now.minuteOfHour,
                    0,
                    0
                )
            }

            fun getDateTime(hourOfDay: Int, minuteOfHour: Int): DateTime {
                val now = DateTime()
                return DateTime(
                    now.year,
                    now.monthOfYear,
                    now.dayOfMonth,
                    hourOfDay,
                    minuteOfHour,
                    0,
                    0
                )
            }
        }

        var selectedTime: ObservableField<DateTime> = ObservableField(getNow(1))

        var started: ObservableField<Boolean> = ObservableField(false)

        fun onSelectTime(hourOfDay: Int, minuteOfHour: Int) {
            selectedTime.set(getDateTime(hourOfDay, minuteOfHour))
        }
    }

    class TimePicker(
        context: Context,
        defaultHourOfDay: Int,
        defaultMinutesOfHour: Int,
        listener: (view: View, hourOfDay: Int, minutesOfHour: Int) -> Unit
    ) :
        TimePickerDialog(
            context,
            OnTimeSetListener { view, hourOfDay, minutesOfHour ->
                listener(view, hourOfDay, minutesOfHour)
            },
            defaultHourOfDay,
            defaultMinutesOfHour,
            true
        )

    private val vmA = NotificationViewModel()
    private val vmB = NotificationViewModel()

    private lateinit var timePickerA: TimePicker
    private lateinit var timePickerB: TimePicker

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.vmA = vmA
        binding.vmB = vmB

        timePickerA = TimePicker(
            this,
            vmA.selectedTime.get()?.hourOfDay ?: 0,
            vmA.selectedTime.get()?.minuteOfHour ?: 0
        ) { _, hourOfDay, minute ->
            vmA.onSelectTime(hourOfDay, minute)
        }
        binding.textViewTimeA.setOnClickListener {
            onClickTimeTextView(vmA, timePickerA)
        }
        binding.buttonSetA.setOnClickListener {
            onClickSetButton(vmA, 1)
        }
        binding.buttonCancelA.setOnClickListener {
            onClickCancelButton(vmA, 1)
        }

        timePickerB = TimePicker(
            this,
            vmB.selectedTime.get()?.hourOfDay ?: 0,
            vmB.selectedTime.get()?.minuteOfHour ?: 0
        ) { _, hourOfDay, minute ->
            vmB.onSelectTime(hourOfDay, minute)
        }
        binding.textViewTimeB.setOnClickListener {
            onClickTimeTextView(vmB, timePickerB)
        }
        binding.buttonSetB.setOnClickListener {
            onClickSetButton(vmB, 2)
        }
        binding.buttonCancelB.setOnClickListener {
            onClickCancelButton(vmB, 2)
        }
    }

    private fun onClickTimeTextView(vm: NotificationViewModel, timePicker: TimePicker) {
        if (vm.started.get()?.not() == true) {
            timePicker.show()
        }
    }

    private fun onClickSetButton(vm: NotificationViewModel, requestCode: Int) {
        vm.started.set(true)
        vm.selectedTime.get()?.let { selectedTime ->
            LocalNotificationUtil.addAlarm(
                this,
                alarmName,
                requestCode,
                selectedTime,
                "指定時刻になりました",
                selectedTime.toString("指定時刻の HH 時 mm 分になりました"),
                "Stand up notification",
                "AlarmManager Tests"
            )
        }
    }

    private fun onClickCancelButton(vm: NotificationViewModel, requestCode: Int) {
        vm.started.set(false)
        LocalNotificationUtil.cancelAlarm(alarmName, requestCode)
    }
}
