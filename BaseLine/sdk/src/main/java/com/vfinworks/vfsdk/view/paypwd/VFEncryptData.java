package com.vfinworks.vfsdk.view.paypwd;

import android.os.Parcel;
import android.os.Parcelable;

/**    
 * Created by cheng on 14/12/15 加密结果封装
 */
public class VFEncryptData implements Parcelable {
	
	private String ciphertext;
    private int length;

    public String getCiphertext() {
        return ciphertext;
    }

    public void setCiphertext(String ciphertext) {
        this.ciphertext = ciphertext;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

	@Override
	public int describeContents() { 
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) { 
		dest.writeString(this.ciphertext);
		dest.writeInt(this.length);
	}
	
	public Creator<VFEncryptData> CREATOR = new Creator<VFEncryptData>() {

		@Override
		public VFEncryptData createFromParcel(Parcel source) { 
			VFEncryptData result = new VFEncryptData();
			result.ciphertext = source.readString();
			result.length = source.readInt(); 
			return result; 
		}

		@Override
		public VFEncryptData[] newArray(int size) { 
			return new VFEncryptData[size];
		}
	};

}
