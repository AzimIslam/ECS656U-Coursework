package com.example.grpc.client.grpcclient;

import java.util.List;
import java.util.*;

import com.example.grpc.server.grpcserver.MatrixMultParallelRequest;
import com.example.grpc.server.grpcserver.MatrixReply;
import com.example.grpc.server.grpcserver.MatrixRequest;
import com.example.grpc.server.grpcserver.MatrixServiceGrpc;
import com.example.grpc.server.grpcserver.PingPongServiceGrpc;
import com.example.grpc.server.grpcserver.PingRequest;
import com.example.grpc.server.grpcserver.PongResponse;
import com.example.grpc.server.grpcserver.Row;

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
			List<Row> C = new ArrayList<Row>();


			int numOfServersRequired = m1.length == 4 ? 4: 8;
			int numberOfRows = m1.length / numOfServersRequired ;


			StreamObserver<MatrixReply> MatrixCallback = new StreamObserver<MatrixReply>() {
				@Override
				public void onNext(MatrixReply value) {
					System.out.println("Received matrix: " +  value);
					List<Row> matrix = value.getCList();
					for (int row = 0; row < matrix.size(); row++) {
						C.add(matrix.get(row));
					}
				}
			
				@Override
				public void onError(Throwable cause) {
					System.out.println("Error occurred: " +  cause.getMessage());
				}
			
				@Override
				public void onCompleted() {
					System.out.println("Stream complete!");
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
					tempRow.setPosition(i);
					requests[serverPtr].addA(tempRow);
				}

				for (int i = row; i < numberOfRows * (serverPtr + 1); i++) {
					Row.Builder tempRow2 = Row.newBuilder();
					for (int col = 0; col < m2.length; col++) {
						tempRow2.addNumber(m2[i][col]);
					}
					requests[serverPtr].addB(tempRow2);
				}

				row += numberOfRows;
				serverPtr += 1;
			}

			for(int i=0; i < stubs.length; i++) {
				stubs[i].addBlock(requests[i].build(), MatrixCallback);
			}

			System.out.println(C.size());

			while(C.size() != m1.length) {
				try {
					TimeUnit.MILLISECONDS.sleep(1000);
				} catch (InterruptedException e) {
					System.out.println(e);
				}
			}

			System.out.println(C.size());

			for (int i = 0; i < C.size();i++) {
				System.out.println(C.get(i));
				for (int j = 0; j < C.size(); j++) {
					resp += C.get(i).getNumber(j) + " ";
				}
				resp += "<br>";
			}

			System.out.println(resp);

		} else {
			/*
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

			System.out.println(request.getAList());


			MatrixReply C=stubs[randomNumber].addBlock(request.build());
			List<Row> arrayListC = C.getCList();

			// Iterates over result matrix
			for (int row = 0; row < arrayListC.size(); row++) {
				for (int col = 0; col < arrayListC.get(row).getNumberList().size(); col++) {
					resp += arrayListC.get(row).getNumber(col) + " ";
				}
				resp += "<br>";
			}
			*/
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
			int numOfServersRequired = m1.length == 4 ? 4: 8;
			int numberOfRows = m1.length / numOfServersRequired;

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

				serverRow += numberOfRows;
				serverPtr += 1;
			}


			MatrixReply[] replies = new MatrixReply[numOfServersRequired];

			for(int i = 0; i < replies.length; i++) {
				replies[i] = stubs[i].parallelMatrixMultiplyBlock(requests[i].build());
			}


			for(int i=0; i < replies.length; i++) {
				for(int j=0; j < replies[i].getCList().size(); j++) {
					for (int k=0; k < replies[i].getCList().get(j).getNumberList().size(); k++) {
						resp += replies[i].getCList().get(j).getNumber(k) + " ";
					}
					resp += "<br>";
				}
			}

		}
		else {
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

			MatrixReply C=stubs[randomNumber].multiplyBlock(request.build());
			List<Row> arrayListC = C.getCList();

			for (int row = 0; row < arrayListC.size(); row++) {
				for (int col = 0; col < arrayListC.get(row).getNumberList().size(); col++) {
					resp += arrayListC.get(row).getNumber(col) + " ";
				}
				resp += "<br>";
			}
		}
		

		return resp;
	}
}
