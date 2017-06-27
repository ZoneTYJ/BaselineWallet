package com.vfinworks.vfsdk.view.paypwd;

import android.os.Parcel;
import android.os.Parcelable;

/**    
 * Created by cheng on 14/12/15 加密结果封装
 */
public class VFEncryptResult implements Parcelable {
	
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
	
	public Creator<VFEncryptResult> CREATOR = new Creator<VFEncryptResult>() {

		@Override
		public VFEncryptResult createFromParcel(Parcel source) { 
			VFEncryptResult result = new VFEncryptResult();
			result.ciphertext = source.readString();
			result.length = source.readInt(); 
			return result; 
		}

		@Override
		public VFEncryptResult[] newArray(int size) { 
			return new VFEncryptResult[size];
		}
	};

}
