package kr.co.lion.memoapplicationproject

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.divider.MaterialDividerItemDecoration
import kr.co.lion.memoapplicationproject.Constant.Companion.MODIFY_RESULT_OK
import kr.co.lion.memoapplicationproject.Constant.Companion.REMOVE_RESULT_OK
import kr.co.lion.memoapplicationproject.databinding.ActivityMainBinding
import kr.co.lion.memoapplicationproject.databinding.RowMainBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// 1st 액티비티 - 메모 관리 액티비티
// 상단 툴바의 + 를 누르면 SecondActivity로 이동.
// RecyclerView의 항목은 메모의 제목과 작성 날짜를 보여줌.
// RecyclerView 항목을 누르면 ThirdActivity로 이동

class MainActivity : AppCompatActivity() {

    lateinit var activityMainBinding: ActivityMainBinding

    // WritingMemoActivity 런처
    lateinit var writingActivityLauncher: ActivityResultLauncher<Intent>

    // ViewMemoActivity 런처
    lateinit var viewActivityLauncher: ActivityResultLauncher<Intent>

    // ModifyingMemoActivity 런처
    lateinit var ModifyingActivityLauncher: ActivityResultLauncher<Intent>

    val memoList = mutableListOf<MemoInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        initData()
        setToolbar()
        setView()
    }

    fun initData() {
        // WritingMemoActivity 런처 계약
        val writeMemoconstract = ActivityResultContracts.StartActivityForResult()
        writingActivityLauncher = registerForActivityResult(writeMemoconstract) {
            // 작업 결과가 OK라면
            if (it.resultCode == RESULT_OK) {
                // 전달된 객체가 있다면
                if (it.data != null) {
                    // 메모 추출
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val memoData = it.data!!.getParcelableExtra("memo", MemoInfo::class.java)
                        memoList.add(memoData!!)
                        activityMainBinding.recyclerViewMain.adapter?.notifyDataSetChanged()
                    } else {
                        val memoData = it.data!!.getParcelableExtra<MemoInfo>("memo")
                        memoList.add(memoData!!)
                        activityMainBinding.recyclerViewMain.adapter?.notifyDataSetChanged()
                    }
                }
            }
        }

        // ViewMemoActivity 런처 계약
        val viewMemocontract = ActivityResultContracts.StartActivityForResult()
        viewActivityLauncher = registerForActivityResult(viewMemocontract) {
            when(it.resultCode) {
                REMOVE_RESULT_OK -> {
                    if(it.data != null) {
                         val time = it.data?.getStringExtra("removeMemo")
                         removeMemo(time)
                    } else {
                        val time = it.data?.getStringExtra("removeMemo")
                        removeMemo(time)
                    }
                }

                MODIFY_RESULT_OK -> {
                    if (it.data != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            val modifedMemo = it.data?.getParcelableExtra("modifiedMemo", MemoInfo::class.java)
                            modifyMemo(modifedMemo!!)
                        } else {
                            val modifedMemo = it.data?.getParcelableExtra<MemoInfo>("modifiedMemo")
                            modifyMemo(modifedMemo!!)
                        }
                    }
                }
            }
        }


        // ModifyingMemoActivity 런처 계약
        val modifymemoconstract = ActivityResultContracts.StartActivityForResult()
        ModifyingActivityLauncher = registerForActivityResult(modifymemoconstract) {

        }

        // RecyclerViewMainAdapter 초기화
        activityMainBinding.recyclerViewMain.adapter = RecyclerViewMainAdapter()
    }

    // 삭제한 메모에 관련한 메소드
    private fun removeMemo(time: String?) {
        val index = memoList.indexOfFirst { it.date == time }
        if (index != -1) {
            memoList.removeAt(index)
            activityMainBinding.recyclerViewMain.adapter?.notifyItemRemoved(index)
            activityMainBinding.recyclerViewMain.adapter?.notifyItemRangeChanged(index, memoList.size)
        }
    }

    // 수정한 메모에 관련한 메소드
    private fun modifyMemo(modifiedMemo: MemoInfo) {
        val index = memoList.indexOfFirst { it.date == modifiedMemo.date }
        if (index != -1) {
            memoList[index].title = modifiedMemo.title
            memoList[index].content = modifiedMemo.content

            activityMainBinding.recyclerViewMain.adapter?.notifyItemChanged(index)
        }
    }

    fun setToolbar() {
        activityMainBinding.apply {
            toolbarManagement.apply {
                // 툴바 타이틀
                title = "메모 관리"

                // 메뉴
                inflateMenu(R.menu.main_menu)

                // 메뉴의 리스너
                setOnMenuItemClickListener {
                    // 메뉴 item으로 분기
                    when (it.itemId) {
                        R.id.main_menu_writing -> {
                            // WritingMemoActivity 실행
                            val writeIntent = Intent(this@MainActivity, WritingMemoActivity::class.java)
                            writingActivityLauncher.launch(writeIntent)
                        }
                    }
                    true
                }
            }
        }
    }

    fun setView() {
        activityMainBinding.apply {
            recyclerViewMain.apply {

                // layoutManager
                layoutManager = LinearLayoutManager(this@MainActivity)

                // 구분선
                val deco = MaterialDividerItemDecoration(this@MainActivity, MaterialDividerItemDecoration.VERTICAL)
                addItemDecoration(deco)
            }
        }
    }

    inner class RecyclerViewMainAdapter : RecyclerView.Adapter<RecyclerViewMainAdapter.ViewHolderMain>() {
        inner class ViewHolderMain(rowMainBinding: RowMainBinding) : RecyclerView.ViewHolder(rowMainBinding.root) {
            val rowMainBinding: RowMainBinding

            init {
                this.rowMainBinding = rowMainBinding

                this.rowMainBinding.root.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

                // RecyclerView 항목을 누르면 ViewMemoActivity로 이동
                this.rowMainBinding.root.setOnClickListener {
                    val viewIntent = Intent(this@MainActivity, ViewMemoActivity::class.java)

                    // 메모 데이터 추가
                    val memoData = memoList[adapterPosition]
                    viewIntent.putExtra("memo", memoData)

                    viewActivityLauncher.launch(viewIntent)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderMain {
            val rowMainBinding = RowMainBinding.inflate(layoutInflater)

            val viewHolderMain = ViewHolderMain(rowMainBinding)

            return viewHolderMain
        }

        override fun getItemCount(): Int {
            return memoList.size
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onBindViewHolder(holder: ViewHolderMain, position: Int) {
            // recyclerView에 제목 보여줌
            holder.rowMainBinding.textViewMemoTitle.text = "${memoList[position].title}"

            // recyclerView에 작성 날짜 보여줌.
            holder.rowMainBinding.textViewMemoWritedDate.text = memoList[position].date
        }
    }
}