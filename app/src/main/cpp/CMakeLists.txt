cmake_minimum_required(VERSION 3.22.1)

include(AndroidNdkModules)

project(LoopsEngine)

set (PARSELIB_DIR ../../../../parselib)
set (IOLIB_DIR ../../../../iolib)

include(${PARSELIB_DIR}/src/main/cpp/CMakeLists.txt)
include(${IOLIB_DIR}/src/main/cpp/CMakeLists.txt)

include_directories(
        ${PARSELIB_DIR}/src/main/cpp
        ${IOLIB_DIR}/src/main/cpp)

add_library(LoopsEngine

        SHARED

        LoopsEngine.cpp
        OboeEngine.cpp
        MyDataCallback.cpp
        )

# Find the Oboe package
find_package(oboe REQUIRED CONFIG)

# Specify the libraries which our native library is dependent on, including Oboe
target_link_libraries(LoopsEngine

        android
        log
        oboe::oboe
        parselib
        iolib
#        flowgraph
        )
