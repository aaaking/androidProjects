/*
 * Copyright (c) 2017-2018 PlayerOne.
 *
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.example.jeliu.eos.data.remote.model.types;

import android.text.TextUtils;

import com.example.jeliu.bipawallet.Common.Constant;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by swapnibble on 2017-09-12.
 */

public class TypeAsset implements EosType.Packer {

    public static final long MAX_AMOUNT = (1 << 62) - 1;

    private long mAmount;
    private double mEosAmount;
    private TypeSymbol mSymbol;

    public TypeAsset(String value) {

        value = value.trim();

        Pattern pattern = Pattern.compile("^([0-9]+)\\.?([0-9]*)([ ][a-zA-Z0-9]{1,7})?$");//\\s(\\w)$");
        Matcher matcher = pattern.matcher(value);

        if (matcher.find()) {
            String beforeDotVal = matcher.group(1), afterDotVal = matcher.group(2);

            String symbolStr = TextUtils.isEmpty(matcher.group(3)) ? null : matcher.group(3).trim();

            mEosAmount = Double.valueOf(beforeDotVal + "." + afterDotVal);
            mAmount = (long) (mEosAmount * Math.pow(10, Constant.SYMBOL_PRECISION));
            mSymbol = new TypeSymbol(afterDotVal.length(), symbolStr);
        } else {
            mEosAmount = this.mAmount = 0;
            this.mSymbol = new TypeSymbol();
        }
    }

    public TypeAsset(double amount) {
        this(amount, new TypeSymbol());
    }

    public TypeAsset(double amount, TypeSymbol symbol) {
        mEosAmount = amount;
        mAmount = (long) (mEosAmount * Math.pow(10, Constant.SYMBOL_PRECISION));
        this.mSymbol = symbol;
    }

    public boolean isAmountInRange() {
        return -MAX_AMOUNT <= mAmount && mAmount <= MAX_AMOUNT;
    }

    public boolean isValid() {
        return isAmountInRange() && (mSymbol != null) && mSymbol.valid();
    }


    public short decimals() {
        return (mSymbol != null) ? mSymbol.decimals() : 0;
    }

    public long precision() {
        return (mSymbol != null) ? mSymbol.precision() : 0;
    }


    public String symbolName() {
        if (mSymbol != null) {
            return mSymbol.name();
        }

        return "";
    }


    public long getAmount() {
        return mAmount;
    }

    @Override
    public String toString() {
        long precisionVal = precision();
        StringBuilder stringBuilder = new StringBuilder("0.");
        for (int i = 0; i < Constant.SYMBOL_PRECISION; i++) {
            stringBuilder.append("0");
        }
        DecimalFormat df = new DecimalFormat(stringBuilder.toString());
        String result = df.format(mEosAmount / precisionVal);

//        if (decimals() > 0) {
//            long fract = mAmount % precisionVal;
//            result += "." + String.valueOf(precisionVal + fract).substring(1);
//        }

        return result + " " + symbolName();
    }

    @Override
    public void pack(EosType.Writer writer) {

        writer.putLongLE(mAmount);

        if (mSymbol != null) {
            mSymbol.pack(writer);
        } else {
            writer.putLongLE(0);
        }
    }

}
