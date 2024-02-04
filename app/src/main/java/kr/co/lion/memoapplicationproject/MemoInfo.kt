package kr.co.lion.memoapplicationproject

import android.os.Parcel
import android.os.Parcelable

// 메모 데이터를 담아둘 mutableList

// 제목, 내용, 날짜를 담을 수 있는 데이터리스트여야 함.
// 날짜는 근데 현재 날짜를 구해서 적용해야 함.
// 그럼 날짜를 데이터에 넣어야 하는건가...??

data class MemoInfo(var title: String?, var content: String?, val date: String?) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(content)
        parcel.writeString(date)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MemoInfo> {
        override fun createFromParcel(parcel: Parcel): MemoInfo {
            return MemoInfo(parcel)
        }

        override fun newArray(size: Int): Array<MemoInfo?> {
            return arrayOfNulls(size)
        }
    }

}