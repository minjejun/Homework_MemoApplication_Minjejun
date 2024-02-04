package kr.co.lion.memoapplicationproject

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import kr.co.lion.memoapplicationproject.Constant.Companion.MODIFY_RESULT_OK
import kr.co.lion.memoapplicationproject.databinding.ActivityModifyingMemoBinding

// 4th 액티비티 - 메모 수정 액티비티
// 수정하기 전의 내용을 TextField에 보여줌
// 수정을 완료하고, "V"를 누르면 ViewActivity의 화면으로 돌아가게 함.
// 이 때, 수정된 내용으로 보여지게 함.
// 작성 날짜는 수정하지 않음.

class ModifyingMemoActivity : AppCompatActivity() {

    lateinit var modifyingMemoBinding: ActivityModifyingMemoBinding

    var selectedMemo: MemoInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        modifyingMemoBinding = ActivityModifyingMemoBinding.inflate(layoutInflater)
        setContentView(modifyingMemoBinding.root)

        // Intent에서 데이터 가져오기
        selectedMemo = intent.getParcelableExtra<MemoInfo>("modifyingMemo")

        setToolbar()
        setTextField()
    }

    fun initData() {

    }

    fun setToolbar() {
        modifyingMemoBinding.apply {

            toolbarModifying.apply {
                title = "메모 수정"

                // 뒤로가기 기능 정의
                setNavigationIcon(R.drawable.arrow_back_24px)
                setNavigationOnClickListener {
                    setResult(RESULT_CANCELED)
                    finish()
                }

                inflateMenu(R.menu.modifying_menu)

                setOnMenuItemClickListener {
                    // 메뉴의 id로 분기
                    when (it.itemId) {
                        // 메모 수정 완료 버튼
                        // 수정된 내용 저장 후 ViewMemoActivity에 변경된 내용 보여주기
                        R.id.complete_modifying_menu -> {
                            selectedMemo?.apply {
                                title = modifyingMemoBinding.textFieldInputTitle3.text.toString()
                                // 날짜는 업데이트하지 않습니다.
                                content = modifyingMemoBinding.textFieldInputContent3.text.toString()
                            }
                            val intent = Intent()
                            intent.putExtra("modifiedMemo", selectedMemo)
                            setResult(MODIFY_RESULT_OK, intent)
                            finish()
                        }
                    }
                    true
                }
            }
        }
    }

    // 텍스트 필드
    fun setTextField() {
        modifyingMemoBinding.apply {
            textFieldInputTitle3.apply {
                setText(selectedMemo?.title)
                isEnabled = true
            }

            textFieldInputContent3.apply {
                setText(selectedMemo?.content)
                isEnabled = true
            }
        }
    }
}