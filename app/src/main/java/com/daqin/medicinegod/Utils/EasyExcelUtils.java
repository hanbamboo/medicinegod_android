package com.daqin.medicinegod.Utils;

import com.alibaba.excel.EasyExcel;
import com.daqin.medicinegod.entity.MedicineTemplate;

import java.util.ArrayList;
import java.util.List;

public class EasyExcelUtils {
    public static boolean writeTemplateFile(String filePath, String sheetName) {
        List<MedicineTemplate> temp = new ArrayList<>();
        MedicineTemplate medicineTemplate = new MedicineTemplate();
        temp.add(medicineTemplate);
        medicineTemplate.clear();
        medicineTemplate.setName("注意事项：1.示例请自行删除，行与行之前不要留空白，否则导入失败！2.如无图片，请输入无（有则填写编码后的图片）。3.除ID与图片外不允许其他项目留空（留空请填无，否则导入失败）。4.请严格按照格式填入导入，请勿留有空行。");
        temp.add(0, medicineTemplate);
        EasyExcel.write(filePath, MedicineTemplate.class)
                .sheet(sheetName)
                .doWrite(temp);
        return Utils.isExists(filePath);
    }
}
