package com.example.grpc.server.grpcserver;


import io.grpc.stub.StreamObserver;

import com.example.grpc.server.grpcserver.MatrixReply;
import com.example.grpc.server.grpcserver.Row;
import net.devh.boot.grpc.server.service.GrpcService;
import java.util.*;

@GrpcService
public class MatrixServiceImpl extends MatrixServiceGrpc.MatrixServiceImplBase
{
	@Override
	public void addBlock(MatrixRequest request, StreamObserver<MatrixReply> reply)
	{
		System.out.println("Request received from client:\n" + request);
		List<Row> A = request.getAList();
		List<Row> B = request.getBList();

		MatrixReply.Builder c = MatrixReply.newBuilder();

		for (int row = 0; row < A.size(); row++) {
			Row.Builder tempRow = Row.newBuilder();
			for (int col = 0; col < A.get(row).getNumberList().size(); col++) {
				Double firstNum = A.get(row).getNumber(col);
				Double secondNum = B.get(row).getNumber(col);
				tempRow.addNumber(firstNum + secondNum);
			};
			c.addC(tempRow);
		}
		MatrixReply response = c.build();
		reply.onNext(response);
		reply.onCompleted();
	}
	@Override
	public void multiplyBlock(MatrixRequest request, StreamObserver<MatrixReply> reply)
	{
		System.out.println("Request received from client:\n" + request);

		List<Row> A = request.getAList();
		List<Row> B = request.getBList();

		MatrixReply.Builder c = MatrixReply.newBuilder();

		// Temporary array for storing matrix
		int[][] tempMatrix = new int[A.size()][A.size()];

		for (int row = 0; row < A.size(); row++) {
			for (int col = 0; col < A.get(row).getNumberList().size(); col++) {
				for (int i = 0; i < A.get(row).getNumberList().size(); i++) {
					Double firstNum = A.get(row).getNumber(i);
					Double secondNum = B.get(i).getNumber(col);
					tempMatrix[row][col] += firstNum * secondNum;
				}
			}
		}

		// Creates the Matrix Reply based on the temporary array
		for (int row = 0; row < tempMatrix.length; row++) {
			Row.Builder tempRow = Row.newBuilder();
			for (int col = 0; col < tempMatrix[row].length; col++) {
				tempRow.addNumber(tempMatrix[row][col]);
			}
			c.addC(tempRow);
		}

		MatrixReply response = c.build();
		reply.onNext(response);
		reply.onCompleted();
	}

	@Override
	public void parallelMatrixMultiplyBlock(MatrixMultParallelRequest request, StreamObserver<MatrixReply> reply) {
		System.out.println("Request received from client:\n" + request);

		List<Row> A = request.getAList();
		List<Row> B = request.getBList();

		MatrixReply.Builder c = MatrixReply.newBuilder();

		// Temporary array for storing matrix
		int[][] tempMatrix = new int[request.getRangeList().get(1) - request.getRangeList().get(0)][A.get(0).getNumberList().size()];


		for (int row = request.getRangeList().get(0); row < request.getRangeList().get(1); row++) {
			System.out.println("Test 1");
			for (int col = 0; col < A.get(row).getNumberList().size(); col++) {
				System.out.println("Test 2");
				for (int i = 0; i < A.get(row).getNumberList().size(); i++) {
					System.out.println("Test 3");
					System.out.println(A.get(row).getNumberList().size());
					System.out.println(i);
					Double firstNum = A.get(row).getNumber(i);
					Double secondNum = B.get(i).getNumber(col);
					tempMatrix[row][col] += firstNum * secondNum;
				}
			}
		}

		// Creates the Matrix Reply based on the temporary array
		for (int row = 0; row < tempMatrix.length; row++) {
			Row.Builder tempRow = Row.newBuilder();
			for (int col = 0; col < tempMatrix[row].length; col++) {
				tempRow.addNumber(tempMatrix[row][col]);
			}
			c.addC(tempRow);
		}

		MatrixReply response = c.build();
		reply.onNext(response);
		reply.onCompleted();
	}		
}
