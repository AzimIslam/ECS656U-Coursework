package com.example.grpc.client.grpcclient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;


@Controller
public class FileUploadController {
    public static String uploadDirectory = System.getProperty("user.dir") + "/uploads";
    @RequestMapping("/")
    public String UploadPage(Model model) {
        return "uploadview";
    }

    public static boolean isPowerOfTwo(int n)
    {
        if(n==0) return false;
        System.out.println((int)(Math.ceil((Math.log(n) / Math.log(2)))) == (int)(Math.floor(((Math.log(n) / Math.log(2))))));
        return (int)(Math.ceil((Math.log(n) / Math.log(2)))) == (int)(Math.floor(((Math.log(n) / Math.log(2)))));
    }
 

    @RequestMapping("/upload")
    public String upload(Model model, @RequestParam("files") MultipartFile[] files) {
        StringBuilder fileNames = new StringBuilder();
        int fileCount = 0;
        for (MultipartFile file: files) {
            Path fileNameAndPath = Paths.get(uploadDirectory,file.getOriginalFilename());
            fileNames.append(file.getOriginalFilename());
            try {
                Files.write(fileNameAndPath, file.getBytes());
                fileCount += 1;
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        if (fileCount == 2) {
            String matrix1 = "";
            String matrix2 = "";

            int[] rows = {0, 0};
            int[] columns = {0, 0};

            for(int i = 0; i < files.length; i++) {
                try {
                    File matrix = new File(uploadDirectory, files[i].getOriginalFilename());
                    Scanner reader = new Scanner(matrix);
                    while (reader.hasNextLine()) {
                        String currentRow = reader.nextLine();
                        if (i == 0) matrix1 += currentRow + "\n";
                        else matrix2 += currentRow + "\n";
                        rows[i] += 1;

                        String[] rowMatrix = currentRow.split(" ");
                        for(int j = 0; j < rowMatrix.length; j++) {
                            try {
                                Double x = Double.parseDouble(rowMatrix[j]);
                                columns[i] += 1;
                            } catch (NumberFormatException e) {
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Matrix must all contains numbers");
                            }
                        }
                    } 
                    reader.close();
                } catch (FileNotFoundException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
            }

            columns[0] = columns[0] / rows[0];
            columns[1] = columns[1] / rows[1];


            if (rows[0] == 0 || rows[1] == 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empty matrix is not accepted");
            else if (rows[0] != columns[0] || rows[1] != columns[1] ) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Both matrices must be square");
            else if (rows[0] != rows[1] || columns[0] != columns[1] ) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Both matrices must be the same dimensions");
            else if (!isPowerOfTwo(rows[0])) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Matrix dimensions must be to the power of two");

            model.addAttribute("msg", "Successfully uploaded files ");
            model.addAttribute("dimensions", rows[0]);
            model.addAttribute("matrix1", matrix1);
            model.addAttribute("m1path", uploadDirectory + "/" + files[0].getOriginalFilename());
            model.addAttribute("matrix2", matrix2);
            model.addAttribute("m2path", uploadDirectory + "/" + files[1].getOriginalFilename());
            model.addAttribute("success", true);
        } else {
            model.addAttribute("msg", "Please only upload two files");
            model.addAttribute("success", false);
        }
        return "uploadstatusview";
    }
}