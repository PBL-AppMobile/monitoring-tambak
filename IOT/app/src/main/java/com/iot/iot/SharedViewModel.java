package com.iot.iot;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<List<SensorData>> suhuData = new MutableLiveData<>();

    public void setSuhuData(List<SensorData> data) {
        suhuData.setValue(data);
    }

    public LiveData<List<SensorData>> getSuhuData() {
        return suhuData;
    }
}
