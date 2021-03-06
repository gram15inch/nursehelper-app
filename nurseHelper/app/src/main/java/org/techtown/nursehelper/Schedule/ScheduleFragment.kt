package org.techtown.nursehelper.Schedule

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.techtown.nursehelper.CalViewItem
import org.techtown.nursehelper.MainActivity
import org.techtown.nursehelper.R
import org.techtown.nursehelper.Schedule.day_item_fragment.DayItemDetailFragment
import org.techtown.nursehelper.Schedule.searchPatientFragment
import org.techtown.nursehelper.calendarviewpager.CalendarPagerAdapter
import org.techtown.nursehelper.databinding.FragmentScheduleBinding
import java.util.*

class ScheduleFragment : Fragment() {
    val binding by lazy{FragmentScheduleBinding.inflate(layoutInflater)}
    lateinit var mainActivity : MainActivity
    var monthItemUpdate : ((MainActivity)->Unit)? = null
   public lateinit var dayItemDetailFragment : DayItemDetailFragment
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        var Adapter = object : CalendarPagerAdapter(mainActivity,this@ScheduleFragment){
            override fun setPrimaryItem(container: ViewGroup, position: Int, obj: Any) {
                super.setPrimaryItem(container, position, obj)
                //보이는 페이지 [년-월]
                this@ScheduleFragment.onBindView(CalViewItem(getCalendar(position).time))
            }
        }
        monthItemUpdate= Adapter.monthItemUpdate
        Log.d("update","calFragment")
        binding.calPager.adapter = Adapter

        /* binding.calPager.onDayClickListener = object : (Day)->Unit{
             override fun invoke(day: Day) {
                 Log.d("tst","calPgLsn: ${day.calendar.time}")
                 val dayItemFragment = DayItemFragment(day,this@CalFragment)
                 mainActivity.supportFragmentManager.beginTransaction().run{
                     replace(R.id.popUpContainer,dayItemFragment)
                     addToBackStack("day_item")
                     commit()
                 }
                 binding.popUpContainer.visibility = View.VISIBLE
                 binding.greyBg.visibility = View.VISIBLE
             }

         }*/




        //일정 추가 버튼
        binding.addScheBtn.setOnClickListener {
             var dayItemDetailFragment = DayItemDetailFragment().apply{
                    pagerAdapterReflesh = Adapter.pagerAdapterReflesh
                    popUpShow = this@ScheduleFragment.popUpShow
            }

            mainActivity.supportFragmentManager.beginTransaction().run{
                replace(R.id.popUpContainer,dayItemDetailFragment)
                addToBackStack("day_item_detail")
                commit()
            }
            popUpShow(1)
        }

        //바탕 클릭시 처음으로
        binding.greyBg.setOnClickListener{
            binding.popUpContainer.visibility = View.INVISIBLE
            binding.greyBg.visibility = View.INVISIBLE
            mainActivity.supportFragmentManager.popBackStack("day_item",1)

            //원래화면으로 돌아올때 재바인딩
            val thread = object : Thread(){
                override fun run() {
                    mainActivity.runOnUiThread(Adapter.pagerAdapterReflesh)
                }
            }
            thread.start()

            true
        }

        //클릭오류 방지
        binding.popUpContainer.setOnTouchListener { v, event ->
            true
        }
        return binding.root
    }
    fun onBindView(calViewItem: CalViewItem){
        var pCal = Calendar.getInstance()
        pCal.time = calViewItem.pryCal
        binding.ymText.text = "${pCal.get(Calendar.YEAR)}-${pCal.get(Calendar.MONTH)+1}"
    }

    val popUpShow = object : (Int)->Unit{
        override fun invoke(p1: Int) {
            if(p1==1) {
                binding.popUpContainer.visibility = View.VISIBLE
                binding.greyBg.visibility = View.VISIBLE
            }else if(p1==0){
                binding.popUpContainer.visibility = View.INVISIBLE
                binding.greyBg.visibility = View.INVISIBLE
            }
        }
    }


}