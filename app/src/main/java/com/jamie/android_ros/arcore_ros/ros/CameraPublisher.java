package com.jamie.android_ros.arcore_ros.ros;

import android.content.ContentProviderClient;
import android.util.Pair;

import com.google.ar.core.Camera;
import com.google.ar.core.CameraIntrinsics;
import com.google.ar.core.Frame;
import com.google.ar.core.PointCloud;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferFactory;
import org.jboss.netty.buffer.ChannelBuffers;
import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMainExecutor;
import org.ros.node.topic.Publisher;

import java.nio.ByteBuffer;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import geometry_msgs.Pose;
import sensor_msgs.PointCloud2;
import sensor_msgs.Image;
import sensor_msgs.CameraInfo;
import sensor_msgs.PointField;
import std_msgs.Char;
import std_msgs.Header;

public class CameraPublisher extends AbstractNodeMain {
    private Publisher<PointCloud2> pointCloudPublisher;
    private Publisher<CameraInfo> cameraInfoPublisher;
    private PointCloud2 pointCloudMsg;
    private CameraInfo cameraInfoMsg;
    private Image depthImage;
    private Image rgbImage;
    private boolean updated;
    private boolean ready;
    ReentrantLock lock;

    public CameraPublisher(final NodeMainExecutor connectedNode) {
        lock = new ReentrantLock();
        initialize();
    }

    private void initialize(){
        updated = false;
    }

    public void update(PointCloud pointCloud, CameraIntrinsics cameraInfo) {
        // rxn formatted {x,y,z,w}
        if(lock.isLocked())
            return;

        lock.lock();

        PointCloud2

        updated = true;

        lock.unlock();
    }

    private PointCloud2 createCloud(Header header, PointField[] fields, float[][] points) {
        String structFormat = getStructFormat(false, fields);
        ChannelBuffer buffer = ChannelBuffers.directBuffer(points.length * 3);
        for(float[] point : points) {
            buffer.writeFloat(point[0]);
            buffer.writeFloat(point[1]);
            buffer.writeFloat(point[2]);
        }

        PointCloud2 cloud = pointCloudPublisher.newMessage();
        cloud.setData(buffer);
        cloud.setFields(ArrayList<PointField>);
        cloud.setHeader(header);
        return new PointCloud2.newMessage();


    }

    cloud_struct = struct.Struct(_get_struct_fmt(False, fields))

    buff = ctypes.create_string_buffer(cloud_struct.size * len(points))

    point_step, pack_into = cloud_struct.size, cloud_struct.pack_into
            offset = 0
     for p in points:
    pack_into(buff, offset, *p)
    offset += point_step

     return PointCloud2(header=header,
                        height=1,
                        width=len(points),
    is_dense=False,
    is_bigendian=False,
    fields=fields,
    point_step=cloud_struct.size,
    row_step=cloud_struct.size * len(points),
    data=buff.raw)

    private PointCloud2 createCloudXYZ32(Header header, float[][] points) {
         PointField[] fields = {
                 new PointFieldImpl("x", 0, PointField.FLOAT32, 1),
                 new PointFieldImpl("y", 4, PointField.FLOAT32, 1),
                 new PointFieldImpl("z", 8, PointField.FLOAT32, 1)
         };

         return createCloud(header, fields, points);
    }

    private String getStructFormat(boolean isBigEndian, PointField[] fields) {
        StringBuilder format  = new StringBuilder(isBigEndian ? ">" : "<");
        int offset = 0;
        java.util.Arrays.sort(fields, new Comparator<PointField>() {
            @Override
            public int compare(PointField pointField, PointField t1) {
                return pointField.getOffset() - t1.getOffset();
            }
        });

        for (PointField field : fields) {
            if (offset < field.getOffset()) {
                for(int j = 0; j < field.getOffset() - offset; ++j)
                    format.append("x");
                offset = field.getOffset();
            }
            if(!DATATYPES.containsKey(field.getDatatype())) {
                System.out.println("Skipping unknown PointField datatype " + field.getDatatype());
            } else {
                Pair<Character, Integer> datatype = DATATYPES.get(field.getDatatype());
                if (datatype != null) {
                    char datatypeFormat = datatype.first;
                    int datatypeLength = datatype.second;
                    for(int j = 0; j < field.getCount(); ++j)
                        format.append(datatypeFormat);
                    offset += field.getCount() * datatypeLength;
                }
            }
        }

        return format.toString();
    }

    private static HashMap<Byte, Pair<Character, Integer>> DATATYPES = new HashMap<>();
     static {
         DATATYPES.put(PointField.INT8, new Pair<>('b', 1));
         DATATYPES.put(PointField.UINT8, new Pair<>('B', 1));
         DATATYPES.put(PointField.INT16, new Pair<>('h', 2));
         DATATYPES.put(PointField.UINT16, new Pair<>('H', 2));
         DATATYPES.put(PointField.INT32, new Pair<>('i', 4));
         DATATYPES.put(PointField.UINT32, new Pair<>('I', 4));
         DATATYPES.put(PointField.FLOAT32, new Pair<>('f', 4));
         DATATYPES.put(PointField.FLOAT64, new Pair<>('d', 8));
     }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("android_depth_camera");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        pointCloudPublisher = connectedNode.newPublisher("android/point_cloud", PointCloud2._TYPE);
        cameraInfoPublisher = connectedNode.newPublisher("android/camera_info", CameraInfo._TYPE);

        // This CancellableLoop will be canceled automatically when the node shuts
        // down.
        connectedNode.executeCancellableLoop(new CancellableLoop() {
            @Override
            protected void setup() {

            }

            @Override
            protected void loop() throws InterruptedException {
                // basically, keep on publishing if data exists
                if(!lock.isLocked() && updated) {
                    lock.lock();
                    pointCloudPublisher.publish(pointCloudMsg);
                    cameraInfoPublisher.publish(cameraInfoMsg);
                    updated = false;
                    lock.unlock();
                }
                //TODO : implement and check publication flags
            }
        });
    }
}
