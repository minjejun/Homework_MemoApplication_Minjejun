package kr.co.lion.memoapplicationproject

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kr.co.lion.memoapplicationproject.Constant.Companion.MODIFY_RESULT_OK
import kr.co.lion.memoapplicationproject.Constant.Companion.REMOVE_RESULT_OK
import kr.co.lion.memoapplicationproject.databinding.ActivityViewMemoBinding

// 3rd 액티비티 - 메모 보기 액티비티
// MainActivity의 RecyclerView에서 선택한 항목의 메모 내용이 보여지게 함.
// 메모의 내용은 TextField를 통해 보여주고, 입력은 불가능하게 함.
// 툴바에는 수정과 삭제가 있고, 아이콘은 자유롭게. 수정을 누르면 ModifyingActivity로 이동.
// 삭제를 누르면 현재 메모에 대해 삭제 처리를 하고 MainActivity로 돌아감.

class ViewMemoActivity : AppCompatActivity() {

    lateinit var viewMemoBinding: ActivityViewMemoBinding

    // ModifyingMemoActivity Launcher
    lateinit var modifyingMemoActivityLauncher : ActivityResultLauncher<Intent>

    var selectedMemo: MemoInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewMemoBinding = ActivityViewMemoBinding.inflate(layoutInflater)
        setContentView(viewMemoBinding.root)

        selectedMemo = intent.getParcelableExtra<MemoInfo>("memo")

        initData()
        setToolbar()
        setWritingTextField()
    }

    fun initData() {
        // ModifyingMemoActivity 런처
        val contract3 = ActivityResultContracts.StartActivityForResult()
        modifyingMemoActivityLauncher = registerForActivityResult(contract3) {
            if(it.resultCode == MODIFY_RESULT_OK) {
                if(it.data != null) {
                    // 메모 추출
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        selectedMemo = it.data!!.getParcelableExtra("modifiedMemo", MemoInfo::class.java)
                    } else {
                        selectedMemo = it.data!!.getParcelableExtra<MemoInfo>("modifiedMemo")
                    }
                    // 텍스트 필드를 업데이트합니다.
                    setModifiedTextField()
                }
            }
        }
    }

    // 툴바에 대한 기능 정하는 메소드
    fun setToolbar() {

        viewMemoBinding.apply {

            toolbarView.apply {
                title = "메모 보기"

                // 뒤로가기 기능 정의
                setNavigationIcon(R.drawable.arrow_back_24px)
                setNavigationOnClickListener {
                    setResult(RESULT_CANCELED)
                    finish()
                }

                inflateMenu(R.menu.view_menu)

                setOnMenuItemClickListener {
                    // 메뉴의 id로 분기
                    when (it.itemId) {
                        // 메모 수정 버튼
                        // ModifyingMemoActivity으로 이동
                        R.id.modifying_memo_menu -> {
                            // ModifyingMemoActivity 실행
                            val modifyingIntent = Intent(this@ViewMemoActivity, ModifyingMemoActivity::class.java)
                            modifyingIntent.putExtra("modifyingMemo", selectedMemo)
                            modifyingMemoActivityLauncher.launch(modifyingIntent)
                        }

                        // 메모 삭제 버튼
                        // 현재 메모에 대해 삭제를 함
                        // MainActivity로 이동
                        R.id.delete_memo_menu -> {
                            // 메모 삭제
                            showDialog("메모 삭제", "메모를 삭제하시겠습니까?")
                        }
                    }
                    true
                }
            }
        }
    }


    // 텍스트 필드에 대한 기능 정하는 메소드
    fun setWritingTextField() {

        // Intent에서 데이터 가져오기
        selectedMemo = intent.getParcelableExtra<MemoInfo>("memo")

        viewMemoBinding.apply {
            textFieldInputTitle2.apply {
                setText(selectedMemo?.title)
                isEnabled = false
            }

            textFieldInputDate.apply {
                setText(selectedMemo?.date)
                isEnabled = false
            }

            textFieldInputContent2.apply {
                setText(selectedMemo?.content)
                isEnabled = false
            }
        }
    }

    // 수정된 메모를 받아오는 메소드
    fun setModifiedTextField() {

        viewMemoBinding.apply {
            textFieldInputTitle2.apply {
                setText(selectedMemo?.title)
                isEnabled = false
            }

            textFieldInputDate.apply {
                setText(selectedMemo?.date)
                isEnabled = false
            }

            textFieldInputContent2.apply {
                setText(selectedMemo?.content)
                isEnabled = false
            }
        }
    }

    // 삭제 하기 전 다이얼로그를 보여주는 메소드
    private fun showDialog(title: String, message: String) {
        val builder = MaterialAlertDialogBuilder(this@ViewMemoActivity).apply {
            setTitle(title)
            setMessage(message)

            setNegativeButton("아니요") { dialogInterface: DialogInterface, i: Int ->

            }

            setPositiveButton("네") { dialogInterface: DialogInterface, i: Int ->
                // memoList에서 이 메모를 삭제해야 함.
                val intent = Intent(this@ViewMemoActivity, MainActivity::class.java)
                intent.putExtra("removeMemo", selectedMemo?.date)
                setResult(REMOVE_RESULT_OK, intent)
                // MainActivity로 돌아감
                finish()
            }
        }
        builder.show()
    }

}