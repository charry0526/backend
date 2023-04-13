package com.xc;

import cn.hutool.core.io.FileUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson.JSON;

import java.io.File;
import java.util.List;

public class Test {

    public static void main(String[] args) {

        File file = FileUtil.file("C://xh_shares.xls");
        ExcelReader reader = ExcelUtil.getReader(file);
        List<List<Object>> read = reader.read();
        for (List<Object> objects : read) {
            Object v1 = objects.get(0);
            Object v2 = objects.get(1);
            System.out.println(JSON.toJSONString(objects));
        }

    }
}
