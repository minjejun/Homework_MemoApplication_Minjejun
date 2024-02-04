package kr.co.lion.memoapplicationproject

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import com.google.android.material.textfield.TextInputEditText
import kr.co.lion.memoapplicationproject.databinding.ActivityWritingMemoBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.concurrent.thread

// 2nd Activity - 메모 작성 액티비티
// 제목과 내용을 입력할 수 있고, "V"를 누르면 MainActivity 화면으로 돌아감.
// 그리고 리사이클러뷰에 보이게 함.
// 이 때, 날짜는 현재 날짜를 구해 사용함.
// 내용은 여러 줄을 입력할 수 있도록 해줌.

class WritingMemoActivity : AppCompatActivity() {

    lateinit var writeBinding: ActivityWritingMemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        writeBinding = ActivityWritingMemoBinding.inflate(layoutInflater)
        setContentView(writeBinding.root)

        setToolbar()
        setView()
    }

    // 툴바에 대한 기능 메소드
    fun setToolbar() {
        writeBinding.apply {
            toolbarWriting.apply {
                title = "메모 작성"

                // Back
                setNavigationIcon(R.drawable.arrow_back_24px)
                setNavigationOnClickListener {
                    setResult(RESULT_CANCELED)
                    finish()
                }

                // 메뉴
                inflateMenu(R.menu.writing_menu)

                setOnMenuItemClickListener {
                    when(it.itemId) {
                        R.id.complete_writing_memo-> {
                            processInputDone()
                        }
                    }
                    true
                }
            }
        }
    }

    // 리사이클러뷰에서 보여지게 하는 메소드
    fun setView() {
        writeBinding.apply {
            textFieldInputTitle.requestFocus()

            showSoftInput(textFieldInputTitle)

            textFieldInputContent.setOnEditorActionListener { v, actionId, event ->
                processInputDone()
                true
            }
        }
    }

    // 입력을 마쳤을 때 실행되는 메소드

    fun processInputDone() {
        writeBinding.apply {
            val title = textFieldInputTitle.text.toString()
            val content = textFieldInputContent.text.toString()

            // 현재 날짜를 가져옵니다.
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val currentDate = sdf.format(Date())

            val memoData = MemoInfo(title, content, currentDate)

            val resultIntent = Intent()

            resultIntent.putExtra("memo", memoData)

            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }


    fun showSoftInput(focusView: TextInputEditText) {
        thread {
            SystemClock.sleep(300)
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(focusView, 0)
        }
    }
}