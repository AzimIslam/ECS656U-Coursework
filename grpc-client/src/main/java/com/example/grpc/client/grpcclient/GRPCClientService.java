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




		MatrixReply C=stub.addBlock(request.build());
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
