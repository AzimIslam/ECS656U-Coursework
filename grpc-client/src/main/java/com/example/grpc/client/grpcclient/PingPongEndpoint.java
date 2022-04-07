package com.example.grpc.client.grpcclient;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;

import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;

import java.io.IOException;

@RestController
public class PingPongEndpoint {    

	GRPCClientService grpcClientService;    
	@Autowired
    	public PingPongEndpoint(GRPCClientService grpcClientService) {
        	this.grpcClientService = grpcClientService;
    	}

	@GetMapping("/ping")
	public String ping() {
		return grpcClientService.ping();
	}
    @PostMapping("/add")
	public String add(@RequestParam String filePath1, @RequestParam String filePath2, @RequestParam int dimensions) {
		double[][] A = new double[dimensions][dimensions];
		double[][] B = new double[dimensions][dimensions];

		int row = 0;

		try {
			File matrix1 = new File(filePath1);
			File matrix2 = new File(filePath2); 
			
			Scanner reader1 = new Scanner(matrix1);
			Scanner reader2 = new Scanner(matrix2);

			while(reader1.hasNextLine()) {
				String currentRow1 = reader1.nextLine();
				String currentRow2 = reader2.nextLine();

				String[] rowMatrix1 = currentRow1.split(" ");
				String[] rowMatrix2 = currentRow2.split(" ");

				for(int col = 0; col < dimensions; col++) {
					A[row][col] = Double.parseDouble(rowMatrix1[col]);
					B[row][col] = Double.parseDouble(rowMatrix2[col]);
				}

				row += 1;
			}
			return grpcClientService.add(A, B);
		} catch(FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
			return "Error occurred";
		}
	}

	@PostMapping("/multiply")
	public String multiply(@RequestParam String filePath1, @RequestParam String filePath2, @RequestParam int dimensions) {
		double[][] A = new double[dimensions][dimensions];
		double[][] B = new double[dimensions][dimensions];

		int row = 0;

		try {
			File matrix1 = new File(filePath1);
			File matrix2 = new File(filePath2); 
			
			Scanner reader1 = new Scanner(matrix1);
			Scanner reader2 = new Scanner(matrix2);

			while(reader1.hasNextLine()) {
				String currentRow1 = reader1.nextLine();
				String currentRow2 = reader2.nextLine();

				String[] rowMatrix1 = currentRow1.split(" ");
				String[] rowMatrix2 = currentRow2.split(" ");

				for(int col = 0; col < dimensions; col++) {
					A[row][col] = Double.parseDouble(rowMatrix1[col]);
					B[row][col] = Double.parseDouble(rowMatrix2[col]);
				}
				

				row += 1;
			}


			return grpcClientService.multiply(A, B);
		} catch(FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
			return "Error occurred";
		}
		
	}
}
