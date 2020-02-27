package com.jamie.android_ros.arcore_ros.ros;

import org.ros.internal.message.RawMessage;

import sensor_msgs.PointField;

public class PointFieldImpl implements PointField {
    private String name;
    private int offset;
    private byte datatype;
    private int count;

    public PointFieldImpl(String _name, int _offset, byte _datatype, int _count) {
        name = _name;
        offset = _offset;
        datatype = _datatype;
        count = _count;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String s) {
        name = s;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public void setOffset(int i) {
        offset = i;
    }

    @Override
    public byte getDatatype() {
        return datatype;
    }

    @Override
    public void setDatatype(byte b) {
        datatype = b;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public void setCount(int i) {
        count = i;
    }

    @Override
    public RawMessage toRawMessage() {
        return null;
    }
}
