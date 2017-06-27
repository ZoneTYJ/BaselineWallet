package com.vfinworks.vfsdk.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

public class VFCardNumberEditText extends EditText {
	
	/* 每组的长度 */
	private Integer eachLength = 4;
	/* 分隔符 */
	private String delimiter = " ";

	private String text = "";

	public VFCardNumberEditText(Context context) {
		super(context);
		init();
	}

	public VFCardNumberEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();

	}

	public VFCardNumberEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	/**
	 * 初始化
	 */
	public void init() {

		// 内容变化监听
		this.addTextChangedListener(new DivisionTextWatcher());
		// 获取焦点监听
		this.setOnFocusChangeListener(new DivisionFocusChangeListener());
	}

	/**
	 * 文本监听
	 * 
	 * @author Administrator
	 * 
	 */
	private class DivisionTextWatcher implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
			String charText = s.toString();  
			// 第一位不允许输入空格或者数字0
			if(charText != null && charText.startsWith(" ")) {
				s.delete(0, 1);
				return;
			}
			// 最后一位不允许输入空格
			if(charText != null && charText.endsWith(" ")) {
				s.delete(charText.length() - 1, charText.length());
				return;
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// 统计个数
			int len = s.length();
			if (len < eachLength)// 长度小于要求的数
				return;
			if (count > 1) {// 设置新字符串的时候，直接返回
				return;
			}
			// 如果包含空格，就清除
			char[] chars = s.toString().replace(" ", "").toCharArray();
			len = chars.length;
			// 每4个分组,加上空格组合成新的字符串
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < len; i++) {
				if (i % eachLength == 0 && i != 0)// 每次遍历到4的倍数，就添加一个空格
				{
					sb.append(" ");
					sb.append(chars[i]);// 添加字符
				} else {
					sb.append(chars[i]);// 添加字符
				}
			}
			// 设置新的字符到文本
			text = sb.toString();
			setText(text);
			setSelection(text.length());
		}
	}

	/**
	 * 获取焦点监听
	 * 
	 * @author Administrator
	 * 
	 */
	private class DivisionFocusChangeListener implements OnFocusChangeListener {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus) {
				// 设置焦点
				setSelection(getText().toString().length());
			}
		}
	}

	/** 得到每组个数 */
	public Integer getEachLength() {
		return eachLength;
	}

	/** 设置每组个数 */
	public void setEachLength(Integer eachLength) {
		this.eachLength = eachLength;
	}

	/** 得到间隔符 */
	public String getDelimiter() {
		return delimiter;
	}

	/** 设置间隔符 */
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

}
