package com.jonas.breathinganalysis;

/**
 * @author Jonas Stein
 * This interface realizes the ability to get notified, when the DataHandler is finished.
 */
interface OnSavingDoneListener {
    /**
     * Gets called when the DataHandler finished its work.
     * @param savingFailed Is true, if the DataHandler was unsuccessful in saving the data.
     */
    void savingDone(boolean savingFailed);
}
