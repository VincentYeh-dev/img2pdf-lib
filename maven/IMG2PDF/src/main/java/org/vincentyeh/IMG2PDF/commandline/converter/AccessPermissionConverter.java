package org.vincentyeh.IMG2PDF.commandline.converter;

import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.vincentyeh.IMG2PDF.commandline.converter.core.BasicConverter;
import picocli.CommandLine;

public class AccessPermissionConverter extends BasicConverter<AccessPermission> {
    @Override
    public AccessPermission convert(String s) throws Exception {
        checkNull(s,getClass().getName()+".s");
        checkEmpty(s,getClass().getName()+".s");
        if(!s.matches("-?[0-9]+")){
            throw new CommandLine.TypeConversionException("AccessPermission contain no-numeric character.");
        }
        int integer=Integer.parseInt(s);
        if(integer>255||integer<0)
            throw new CommandLine.TypeConversionException("AccessPermission out of range[0-255].");

        byte data=(byte)integer;
        boolean[] value=new boolean[8];

        for(int i=0;i<8;i++){
            value[i]=(data&128)!=0;
            data<<=1;
        }
        AccessPermission ap=new AccessPermission();
        ap.setCanAssembleDocument(value[0]);
        ap.setCanExtractContent(value[1]);
        ap.setCanExtractForAccessibility(value[2]);
        ap.setCanFillInForm(value[3]);
        ap.setCanModify(value[4]);
        ap.setCanModifyAnnotations(value[5]);
        ap.setCanPrint(value[6]);
        ap.setCanPrintDegraded(value[7]);
        return ap;
    }
}
