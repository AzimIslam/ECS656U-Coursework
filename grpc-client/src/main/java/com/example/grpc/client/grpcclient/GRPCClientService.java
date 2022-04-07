package com.example.grpc.client.grpcclient;

import java.util.List;

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
		final String[] IP_ADDR = {"35.224.36.36", "34.135.118.75", "35.225.249.142",  "34.123.163.45", "34.123.200.106", "35.222.69.89", "34.66.62.10", "34.72.67.235"};
		
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
			int numberOfRows = m1.length / numOfServersRequired ;

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

				row += numberOfRows;
				serverPtr += 1;
			}

			MatrixReply[] replies = new MatrixReply[numOfServersRequired];

			for(int i=0; i < replies.length; i++) {
				replies[i] = stubs[i].addBlock(requests[i].build());
			}

			for(int i=0; i < replies.length; i++) {
				for(int j=0; j < replies[i].getCList().size(); j++) {
					for (int k=0; k < replies[i].getCList().get(j).getNumberList().size(); k++) {
						resp += replies[i].getCList().get(j).getNumber(k) + " ";
					}
					resp += "<br>";
				}
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
		}

		return resp;
		
    }

	public String multiply(double[][] m1, double[][] m2) {
		final String[] IP_ADDR = {"35.224.36.36", "34.135.118.75", "35.225.249.142",  "34.123.163.45", "34.123.200.106", "35.222.69.89", "34.66.62.10", "34.72.67.235"};
		
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
			int row = 0;

			while(serverPtr < numOfServersRequired) {
				requests[serverPtr].addRange(row);
				requests[serverPtr].addRange(row+numberOfRows);
				for (int i = 0; i < m1.length; i++) {
					Row.Builder tempRow = Row.newBuilder();
					for (int col = 0; col < m1[i].length; col++) {
						tempRow.addNumber(m1[i][col]);
					}
					requests[serverPtr].addA(tempRow);
				}

				for (int i = 0; i < m2.length; i++) {
					Row.Builder tempRow2 = Row.newBuilder();
					for (int col = 0; col < m2[i].length; col++) {
						tempRow2.addNumber(m2[i][col]);
					}
					requests[serverPtr].addB(tempRow2);
				}

				row += numberOfRows;
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
