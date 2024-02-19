package com.smartcontact.smartcontactmanager.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
@Component
public class FileUploader {
     
     public boolean uploadFile(String path,MultipartFile file){
            boolean flag=false;
            String name= file.getOriginalFilename();

            String filePath=path+File.separator+name;

            File f= new File(path);

            if(!f.exists()){
                f.mkdirs();
            }

            try {
                Files.copy(file.getInputStream(), Paths.get(filePath) ,null);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return flag=true;
     }
    
}
