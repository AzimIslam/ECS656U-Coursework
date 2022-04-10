package com.example.grpc.client.grpcclient;

import java.util.*;

import com.example.grpc.server.grpcserver.MatrixMultParallelRequest;
import com.example.grpc.server.grpcserver.MatrixReply;
import com.example.grpc.server.grpcserver.MatrixRequest;
import com.example.grpc.server.grpcserver.MatrixServiceGrpc;
import com.example.grpc.server.grpcserver.PingPongServiceGrpc;
import com.example.grpc.server.grpcserver.PingRequest;
import com.example.grpc.server.grpcserver.PongResponse;
import com.example.grpc.server.grpcserver.Row;
import com.example.grpc.server.grpcserver.MatrixServiceGrpc.MatrixServiceBlockingStub;

import org.springframework.stereotype.Service;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.*;





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

	public boolean checkLocks(int[] locks) {
		if (locks[0] != 0) System.out.println("Waiting for " + String.valueOf(locks[0]) + " servers to complete...");
		else System.out.println("All servers have completed their operations");
		return locks[0] != 0;
	}

    public String add(double[][] m1, double[][] m2){
		final String[] IP_ADDR = {"34.132.180.67", "34.66.112.58", "104.154.23.30",  "104.154.128.69", "35.188.26.202", "104.154.156.149", "35.239.214.67", "34.67.187.208"};
		
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

		final MatrixServiceGrpc.MatrixServiceStub[] stubs = {
			MatrixServiceGrpc.newStub(channels[0]),
			MatrixServiceGrpc.newStub(channels[1]),
			MatrixServiceGrpc.newStub(channels[2]),
			MatrixServiceGrpc.newStub(channels[3]),
			MatrixServiceGrpc.newStub(channels[4]),
			MatrixServiceGrpc.newStub(channels[5]),
			MatrixServiceGrpc.newStub(channels[6]),
			MatrixServiceGrpc.newStub(channels[7]),
		};

		String resp = "";


		if (m1.length > 2) {
			int numOfServersRequired = m1.length == 4 ? 4: 8;
			int numberOfRows = m1.length / numOfServersRequired;


			String[] responses = new String[numOfServersRequired];

			int[] locks = new int[]{numOfServersRequired};


			StreamObserver<MatrixReply> MatrixCallback = new StreamObserver<MatrixReply>() {
				@Override
				public void onNext(MatrixReply value) {
					String matrix = value.getC();
					responses[value.getPosition()-1] = matrix;
				}
			
				@Override
				public void onError(Throwable cause) {
					System.out.println("Error occurred: " +  cause.toString());
				}
			
				@Override
				public void onCompleted() {
					locks[0] -= 1;
					System.out.println("A server has completed adding " + String.valueOf(numberOfRows) +  " rows");
				}
			};

			MatrixRequest.Builder[] requests = new MatrixRequest.Builder[numOfServersRequired];

			for (int i = 0; i < requests.length; i++) {
				requests[i] = MatrixRequest.newBuilder();
			}

			int serverPtr = 0;
			int row = 0;

			while(serverPtr < numOfServersRequired) {
				for (int i = row; i < row + numberOfRows; i++) {
					Row.Builder tempRow = Row.newBuilder();
					for (int col = 0; col < m1.length; col++) {
						tempRow.addNumber(m1[i][col]);
					}
					requests[serverPtr].addA(tempRow);
				}

				for (int i = row; i < numberOfRows * (serverPtr + 1); i++) {
					Row.Builder tempRow2 = Row.newBuilder();
					for (int col = 0; col < m2.length; col++) {
						tempRow2.addNumber(m2[i][col]);
					}
					requests[serverPtr].addB(tempRow2);
				}

				requests[serverPtr].setPosition(serverPtr + 1);

				row += numberOfRows;
				serverPtr += 1;
			}

			for(int i=0; i < numOfServersRequired; i++) {
				stubs[i].addBlock(requests[i].build(), MatrixCallback);
				try {
					TimeUnit.MILLISECONDS.sleep((int)(Math.random()*1000));
				} catch (InterruptedException e) {
					System.out.println(e);
				}
			}


			while(checkLocks(locks)) {
				try {
					TimeUnit.MILLISECONDS.sleep(1000);
				} catch (InterruptedException e) {
					System.out.println(e);
				}
			}

			for (int i = 0; i < responses.length; i++) {
				resp += responses[i];
			}

		} else {
			int randomNumber = (int)(Math.random()*8);
			ManagedChannel channel = ManagedChannelBuilder.forAddress(IP_ADDR[randomNumber], 9090).usePlaintext().build();

			MatrixServiceGrpc.MatrixServiceBlockingStub stub = MatrixServiceGrpc.newBlockingStub(channel);

			
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

			System.out.println(request.getAList());


			MatrixReply C=stub.addBlock(request.build());

			resp = C.getC();
		}

		return resp;
		
    }

	public String multiply(double[][] m1, double[][] m2) {
		final String[] IP_ADDR = {"34.132.180.67", "34.66.112.58", "104.154.23.30",  "104.154.128.69", "35.188.26.202", "104.154.156.149", "35.239.214.67", "34.67.187.208"};		
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

		final MatrixServiceGrpc.MatrixServiceStub[] stubs = {
			MatrixServiceGrpc.newStub(channels[0]),
			MatrixServiceGrpc.newStub(channels[1]),
			MatrixServiceGrpc.newStub(channels[2]),
			MatrixServiceGrpc.newStub(channels[3]),
			MatrixServiceGrpc.newStub(channels[4]),
			MatrixServiceGrpc.newStub(channels[5]),
			MatrixServiceGrpc.newStub(channels[6]),
			MatrixServiceGrpc.newStub(channels[7]),
		};

		String resp="";

		if (m1.length > 2) {
			int numOfServersRequired = m1.length == 4 ? 4: 8;
			int numberOfRows = m1.length / numOfServersRequired;

			String[] responses = new String[numOfServersRequired];

			int[] locks = new int[]{numOfServersRequired};

			StreamObserver<MatrixReply> MatrixCallback = new StreamObserver<MatrixReply>() {
				@Override
				public void onNext(MatrixReply value) {
					String matrix = value.getC();
					responses[value.getPosition()-1] = matrix;
				}
			
				@Override
				public void onError(Throwable cause) {
					System.out.println("Error occurred: " +  cause.toString());
				}
			
				@Override
				public void onCompleted() {
					locks[0] -= 1;
					System.out.println("A server has completed adding " + String.valueOf(numberOfRows) +  " rows");
				}
			};

			MatrixMultParallelRequest.Builder[] requests = new MatrixMultParallelRequest.Builder[numOfServersRequired];

			for (int i = 0; i < requests.length; i++) {
				requests[i] = MatrixMultParallelRequest.newBuilder();
			}

			int serverPtr = 0;
			int serverRow = 0;

			while(serverPtr < numOfServersRequired) {
				requests[serverPtr].addRange(serverRow);
				requests[serverPtr].addRange(serverRow+numberOfRows);
				for (int row = 0; row < m1.length; row++) {
					Row.Builder tempRow = Row.newBuilder();
					for (int col = 0; col < m1[row].length; col++) {
						tempRow.addNumber(m1[row][col]);
					}
					requests[serverPtr].addA(tempRow);
				}
	
				for (int row = 0; row < m2.length; row++) {
					Row.Builder tempRow = Row.newBuilder();
					for (int col = 0; col < m2[row].length; col++) {
						tempRow.addNumber(m2[row][col]);
					}
					requests[serverPtr].addB(tempRow);
				}

				requests[serverPtr].setPosition(serverPtr+1);

				serverRow += numberOfRows;
				serverPtr += 1;
			}

			for(int i = 0; i < stubs.length; i++) {
				stubs[i].parallelMatrixMultiplyBlock(requests[i].build(), MatrixCallback);
			}

			while(checkLocks(locks)) {
				try {
					TimeUnit.MILLISECONDS.sleep(1000);
				} catch (InterruptedException e) {
					System.out.println(e);
				}
			}

			for (int i = 0; i < responses.length; i++) {
				resp += responses[i];
			}

		}
		else {
			int randomNumber = (int)(Math.random()*8);
			ManagedChannel channel = ManagedChannelBuilder.forAddress(IP_ADDR[randomNumber], 9090).usePlaintext().build();

			MatrixRequest.Builder request = MatrixRequest.newBuilder();

			MatrixServiceGrpc.MatrixServiceBlockingStub stub = MatrixServiceGrpc.newBlockingStub(channel);

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

			resp = C.getC();
		}
		

		return resp;
	}
}
