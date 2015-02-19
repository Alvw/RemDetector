package dreamrec;

import bdf.RecordingBdfConfig;

import java.io.File;

/**
 * Created by mac on 30/11/14.
 */
public class RecordingSettings {
    private String[] channelsLabels;
    private int[] channelsFrequencies;
    private String patientIdentification;
    private String recordingIdentification;
    private boolean[] activeChannels;
    private String directoryToSave;
    private String filename;

    public RecordingSettings(String[] channelsLabels) {
        this.channelsLabels = channelsLabels;
        activeChannels = new boolean[channelsLabels.length];
        channelsFrequencies = new int[channelsLabels.length];
    }

    public RecordingSettings(RecordingBdfConfig recordingBdfConfig) {
        this(recordingBdfConfig.getSignalsLabels());
        patientIdentification = recordingBdfConfig.getPatientIdentification();
        recordingIdentification = recordingBdfConfig.getRecordingIdentification();
        channelsFrequencies = recordingBdfConfig.getNormalizedSignalsFrequencies();
    }

    public String getDirectoryToSave() {
        return directoryToSave;
    }

    public void setDirectoryToSave(String directoryToSave) {
        this.directoryToSave = directoryToSave;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String[] getChannelsLabels() {
        return channelsLabels;
    }

    public boolean[] getActiveChannels() {
        return activeChannels;
    }

    public int[] getChannelsFrequencies() {
        return channelsFrequencies;
    }

    public void setActiveChannels(boolean[] activeChannels) {
        int length = Math.min(this.activeChannels.length, activeChannels.length);
        for (int i = 0; i < length; i++) {
            this.activeChannels[i] = activeChannels[i];
        }
    }

    public void setChannelsFrequencies(int[] channelsFrequencies) {
        int length = Math.min(this.channelsFrequencies.length, channelsFrequencies.length);
        for (int i = 0; i < length; i++) {
            this.channelsFrequencies[i] = channelsFrequencies[i];
        }
    }

    public String getPatientIdentification() {
        return patientIdentification;
    }

    public void setPatientIdentification(String patientIdentification) {
        this.patientIdentification = patientIdentification;
    }

    public String getRecordingIdentification() {
        return recordingIdentification;
    }

    public void setRecordingIdentification(String recordingIdentification) {
        this.recordingIdentification = recordingIdentification;
    }
}
