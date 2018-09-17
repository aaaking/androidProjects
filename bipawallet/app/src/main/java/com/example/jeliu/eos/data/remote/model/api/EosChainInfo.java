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
package com.example.jeliu.eos.data.remote.model.api;

/**
 * Created by swapnibble on 2017-09-08.
 */

import com.google.gson.annotations.Expose;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EosChainInfo {

    @Expose
    public String server_version;

    @Expose
    public Integer head_block_num;

    @Expose
    public Integer last_irreversible_block_num;

    @Expose
    public String head_block_id;

    @Expose
    public String head_block_time;

    @Expose
    public String head_block_producer;

    @Expose
    public String chain_id;

    @Expose
    public long virtual_block_cpu_limit;

    @Expose
    public long virtual_block_net_limit;

    @Expose
    public long block_cpu_limit;

    @Expose
    public long block_net_limit;

    public String getTimeAfterHeadBlockTime(int diffInMilSec) {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date date = sdf.parse( this.head_block_time);

            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add( Calendar.MILLISECOND, diffInMilSec);
            date = c.getTime();

            return sdf.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            return this.head_block_time;
        }
    }

    public String getBrief(){
        return    "server_version: "  + server_version
                + "\nhead block num: " + head_block_num
                + "\nlast irreversible block: " + last_irreversible_block_num
                + "\nhead block time: " + head_block_time
                + "\nhead block producer: " + head_block_producer ;
    }
}
