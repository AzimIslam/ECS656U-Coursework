package com.example.grpc.client.grpcclient;

import com.example.grpc.server.grpcserver.PingRequest;
import com.example.grpc.server.grpcserver.PongResponse;
import com.example.grpc.server.grpcserver.PingPongServiceGrpc;
import com.example.grpc.server.grpcserver.MatrixRequest;
import com.example.grpc.server.grpcserver.MatrixReply;
import com.example.grpc.server.grpcserver.MatrixServiceGrpc;
import com.example.grpc.server.grpcserver.Row;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.*;
import java.lang.Math;

@Service
public class GRPCClientService {
    public String ping() {

			// coursework-2 instance
        	ManagedChannel channel = ManagedChannelBuilder.forAddress("10.128.0.12", 9090)
                .usePlaintext()
                .build();        
		PingPongServiceGrpc.PingPongServiceBlockingStub stub
                = PingPongServiceGrpc.newBlockingStub(channel);        
		PongResponse helloResponse = stub.ping(PingRequest.newBuilder()
                .setPing("")
                .build());        
		channel.shutdown();        
		return helloResponse.getPong();
    }
    public String add(double[][] m1, double[][] m2){
		final String[] IP_ADDR = {"10.128.0.12", "10.128.0.13", "10.128.0.14",  "10.128.0.15", "10.128.0.16", "10.128.0.2", "10.128.0.20", "10.128.0.21"};
		
		final ManagedChannel[] channels = {
			ManagedChannelBuilder.forAddress(IP_ADDR[0], 9090).usePlaintext().build(), 
			ManagedChannelBuilder.forAddress(IP_ADDR[1], 9090).usePlaintext().build(),
			ManagedChannelBuilder.forAddress(IP_ADDR[2], 9090).usePlaintext().build(),
			ManagedChannelBuilder.forAddress(IP_ADDR[3], 9090).usePlaintext().build(),
			ManagedChannelBuilder.forAddress(IP_ADDR[4], 9090).usePlaintext().build(),
			ManagedChannelBuilder.forAddress(IP_ADDR[5], 9090).usePlaintext().build(),
			ManagedChannelBuilder.forAddress(IP_ADDR[6], 9090).usePlaintext().build(),
			ManagedChannelBuilder.forAddress(IP_ADDR[7], 9090).usePlaintext().build()
		};

		final MatrixServiceGrpc.MatrixServiceBlockingStub[] stubs = {
			MatrixServiceGrpc.newBlockingStub(channels[0]),
			MatrixServiceGrpc.newBlockingStub(channels[1]),
			MatrixServiceGrpc.newBlockingStub(channels[2]),
			MatrixServiceGrpc.newBlockingStub(channels[3]),
			MatrixServiceGrpc.newBlockingStub(channels[4]),
			MatrixServiceGrpc.newBlockingStub(channels[5]),
			MatrixServiceGrpc.newBlockingStub(channels[6]),
			MatrixServiceGrpc.newBlockingStub(channels[7]),
		};

		String resp="";

		if (m1.length > 2) {
			int numberOfRows = m1.length / IP_ADDR.length;
			int numOfServersRequired = m1.length == 4 ? 4: 8;

			MatrixRequest.Builder[] requests = new MatrixRequest.Builder[numOfServersRequired];

			for (int i = 0; i < requests.length; i++) {
				requests[i] = MatrixRequest.newBuilder();
			}

			/*
			for (int server=0; server < numOfServersRequired; server++) {
				int startingPos = 0;
				int endingPos = 0;
				for (int row = 0; row < m1.length; row++) {
					Row.Builder tempRow = Row.newBuilder();
					for (int col = 0; col < m1[row].length; col++) {
						tempRow.addNumber(m1[row][col]);
					}
					requests[server].addA(tempRow);
				}
	
				for (int row = 0; row < m2.length; row++) {
					Row.Builder tempRow = Row.newBuilder();
					for (int col = 0; col < m2[row].length; col++) {
						tempRow.addNumber(m2[row][col]);
					}
					requests[server].addA(tempRow);
				}
			}*/

			int serverPtr = 0;
			int row = 0;

			while(serverPtr < numOfServersRequired) {
				for (int i = row; i < numberOfRows * (serverPtr + 1); i++) {
					Row.Builder tempRow = Row.newBuilder();
					for (int col = 0; col < m1[i].length; col++) {
						tempRow.addNumber(m1[row][col]);
					}
					requests[serverPtr].addA(tempRow);
				}

				for (int i = row; i < numberOfRows * (serverPtr + 1); i++) {
					Row.Builder tempRow2 = Row.newBuilder();
					for (int col = 0; col < m2[i].length; col++) {
						tempRow2.addNumber(m2[row][col]);
					}
					requests[serverPtr].addB(tempRow2);
				}

				row += numberOfRows;
				serverPtr += 1;
			}

		} else {
			int randomNumber = (int)(Math.random()*8);

			MatrixRequest.Builder request = MatrixRequest.newBuilder();

			for (int row = 0; row < m1.length; row++) {
				Row.Builder tempRow = Row.newBuilder();
				for (int col = 0; col < m1[row].length; col++) {
					tempRow.addNumber(m1[row][col]);
				}
				request.addA(tempRow);
			}

			for (int row = 0; row < m2.length; row++) {
				Row.Builder tempRow = Row.newBuilder();
				for (int col = 0; col < m2[row].length; col++) {
					tempRow.addNumber(m2[row][col]);
				}
				request.addB(tempRow);
			}




			MatrixReply C=stubs[randomNumber].addBlock(request.build());
			List<Row> arrayListC = C.getCList();

			// Iterates over result matrix

			for (int row = 0; row < arrayListC.size(); row++) {
				for (int col = 0; col < arrayListC.get(row).getNumberList().size(); col++) {
					resp += arrayListC.get(row).getNumber(col) + " ";
				}
				resp += "<br>";
			}
		}

		return resp;
		
    }

	public String multiply(double[][] m1, double[][] m2) {
		ManagedChannel channel = ManagedChannelBuilder.forAddress("10.128.0.12",9090)
		.usePlaintext()
		.build();
		MatrixServiceGrpc.MatrixServiceBlockingStub stub
		 = MatrixServiceGrpc.newBlockingStub(channel);

		MatrixRequest.Builder request = MatrixRequest.newBuilder();

		for (int row = 0; row < m1.length; row++) {
			Row.Builder tempRow = Row.newBuilder();
			for (int col = 0; col < m1[row].length; col++) {
				tempRow.addNumber(m1[row][col]);
			}
			request.addA(tempRow);
		}

		for (int row = 0; row < m2.length; row++) {
			Row.Builder tempRow = Row.newBuilder();
			for (int col = 0; col < m2[row].length; col++) {
				tempRow.addNumber(m2[row][col]);
			}
			request.addB(tempRow);
		}




		MatrixReply C=stub.multiplyBlock(request.build());
		List<Row> arrayListC = C.getCList();

		// Iterates over result matrix
		String resp="";

		for (int row = 0; row < arrayListC.size(); row++) {
			for (int col = 0; col < arrayListC.get(row).getNumberList().size(); col++) {
				resp += arrayListC.get(row).getNumber(col) + " ";
			}
			resp += "<br>";
		}

		return resp;
}
}
