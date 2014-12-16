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
    private File file;

    public RecordingSettings(String[] channelsLabels) {
        this.channelsLabels = channelsLabels;
        activeChannels = new boolean[channelsLabels.length];
    }

    public RecordingSettings(RecordingBdfConfig recordingBdfConfig) {
        this(recordingBdfConfig.getSignalsLabels());
        patientIdentification = recordingBdfConfig.getPatientIdentification();
        recordingIdentification = recordingBdfConfig.getRecordingIdentification();
        channelsFrequencies = recordingBdfConfig.getNormalizedSignalsFrequencies();
    }

    public String[] getChannelsLabels() {
        return channelsLabels;
    }

    public int[] getChannelsFrequencies() {
        return channelsFrequencies;
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

    public boolean[] getActiveChannels() {
        return activeChannels;
    }

    public void setActiveChannels(boolean[] activeChannels) {
        int length = Math.min(this.activeChannels.length, activeChannels.length);
        for (int i = 0; i < length; i++) {
            this.activeChannels[i] = activeChannels[i];
        }
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
