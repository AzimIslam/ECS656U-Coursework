// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: matrix.proto

package com.example.grpc.server.grpcserver;

public interface MatrixRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:matrixmult.MatrixRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>repeated .matrixmult.Row a = 1;</code>
   */
  java.util.List<com.example.grpc.server.grpcserver.Row> 
      getAList();
  /**
   * <code>repeated .matrixmult.Row a = 1;</code>
   */
  com.example.grpc.server.grpcserver.Row getA(int index);
  /**
   * <code>repeated .matrixmult.Row a = 1;</code>
   */
  int getACount();
  /**
   * <code>repeated .matrixmult.Row a = 1;</code>
   */
  java.util.List<? extends com.example.grpc.server.grpcserver.RowOrBuilder> 
      getAOrBuilderList();
  /**
   * <code>repeated .matrixmult.Row a = 1;</code>
   */
  com.example.grpc.server.grpcserver.RowOrBuilder getAOrBuilder(
      int index);

  /**
   * <code>repeated .matrixmult.Row b = 2;</code>
   */
  java.util.List<com.example.grpc.server.grpcserver.Row> 
      getBList();
  /**
   * <code>repeated .matrixmult.Row b = 2;</code>
   */
  com.example.grpc.server.grpcserver.Row getB(int index);
  /**
   * <code>repeated .matrixmult.Row b = 2;</code>
   */
  int getBCount();
  /**
   * <code>repeated .matrixmult.Row b = 2;</code>
   */
  java.util.List<? extends com.example.grpc.server.grpcserver.RowOrBuilder> 
      getBOrBuilderList();
  /**
   * <code>repeated .matrixmult.Row b = 2;</code>
   */
  com.example.grpc.server.grpcserver.RowOrBuilder getBOrBuilder(
      int index);
}
