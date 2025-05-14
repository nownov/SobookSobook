/*
어시슈트 - 소북소북 코드입니다.

정보보호학과 2020111323 김지원
정보보호학과 2021111325 김해린
정보보호학과 2021111336 송다은(팀 대표)
정보보호학과 2021111694 이가연

 */

package com.android.bookdiary

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.fragment_daily_memo.*

class DailyMemoFragment : Fragment() {

    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_daily_memo, container, false)

        // DailyFragment에서 전달한 값 받아오기
        var dDate = arguments?.getString("dDate")
        var dTitle = arguments?.getString("dTitle")
        var dColor = arguments?.getString("dColor")
        var dTotalPage = arguments?.getString("dTotalPage")
        var accumPage = arguments?.getInt("dAccumPage")

        // 작성 완료 버튼을 클릭했을 때
        val dailyDoneBtn = view.findViewById<Button>(R.id.dailyDoneBtn)
        dailyDoneBtn.setOnClickListener {

            val mainFragment = MainFragment()
            val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

            var page = editTextNumber.text.toString()

            var totalPage = dTotalPage!!.toInt()
            var accumPageInt : Int = accumPage!!.toInt()

            // 만약 페이지의 수가 비어있지 않고, 현재까지 읽은 페이지의 수와 오늘 읽은 페이지의 합이 총 페이지 수보다 클 때
            // 또는 페이지의 수가 비어있지 않고, 오늘 읽은 페이지 수가 총 페이지 수보다 클 때
            // 독후감 입력을 완료할 수 없다는 팝업창 띄우기
            if((page != "" && accumPageInt + page.toInt() > totalPage) || (page != "" && page.toInt() > totalPage) ){

                val mDialogView = LayoutInflater.from(context).inflate(R.layout.daily_page_dialog, null, false)
                val mBuilder = AlertDialog.Builder(context)
                    .setView(mDialogView)
                    .setTitle("완료할 수 없어요")
                val  mAlertDialog = mBuilder.show()
                val parent = mDialogView.parent as ViewGroup
                val btn = mDialogView.findViewById<Button>(R.id.dialogBtn)
                btn.setOnClickListener {
                    parent.removeView(mDialogView)
                    mAlertDialog.dismiss()
                }
            }

            // 정상적인 페이지 수 입력시에만 DB로 입력 값 전달하기
            else if (page != "") {
                var dPage = page.toInt()
                accumPage = accumPage?.plus(dPage)
                var dSentence: String = likeSentence.text.toString()
                var dThink: String = myThink.text.toString()

                dbManager = DBManager(activity, "bookDB", null, 1)
                sqlitedb = dbManager.writableDatabase

                // writeDB에 오늘의 독후감 내용 기록하고, 책의 제목을 이용하여 bookDB에 오늘 읽은 페이지 수, 지금까지 읽은 페이지의 총합 업데이트하기
                sqlitedb.execSQL("INSERT INTO writeDB VALUES ('" + dPage + "', '" + dSentence + "', '" + dThink + "', '" + dDate + "', '" + dTitle + "', '" + dColor + "', '" + dTotalPage + "');")
                sqlitedb.execSQL("UPDATE bookDB SET nowPage = '" + dPage + "', accumPage = '" + accumPage + "' WHERE ( title = '" + dTitle + "');")

                sqlitedb.close()
                dbManager.close()

                transaction.replace(R.id.container, mainFragment)
                transaction.commit()
            }

            // 페이지 수 미입력시 완료 불가하다는 팝업 띄우기
            else {
                val mDialogView = LayoutInflater.from(context).inflate(R.layout.daily_null_dialog, null, false)
                val mBuilder = AlertDialog.Builder(context)
                    .setView(mDialogView)
                    .setTitle("완료할 수 없어요")
                val  mAlertDialog = mBuilder.show()
                val parent = mDialogView.parent as ViewGroup
                val btn = mDialogView.findViewById<Button>(R.id.dialogBtn)
                btn.setOnClickListener {
                    parent.removeView(mDialogView)
                    mAlertDialog.dismiss()
                }
            }
        }

        // 책 다시 선택하도록 돌아가는 버튼을 눌렀을 때
        val dailyBackBtn = view.findViewById<Button>(R.id.doneBtn)
        dailyBackBtn.setOnClickListener {
            val dailyFragment = DailyFragment()

            // DailyFragment로 돌아가도 날짜 정보가 유지되도록 bundle로 값 전달하기
            var bundle = Bundle()
            bundle.putString("dDate", dDate)
            val ft : FragmentTransaction = activity?.supportFragmentManager!!.beginTransaction()
            dailyFragment.arguments = bundle
            ft.replace(R.id.container, dailyFragment).commit()
            Toast.makeText(activity, dDate, Toast.LENGTH_SHORT).show() //날짜 정보 토스트 메세지로 띄우기
        }

        return view

    }

}
