/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jcpsim.scenarios;

/**
 *
 * @author esteban
 */
public class VO2CalculationDataSnaphot {

    
    //Ventilator
    private double vFrequency;
    private double vFIO2;
    private double vPEEP;
    private double vPIP;
    private double vPauseTime;
    private double vFlowRate;
    private double vInspiratoryTime;
    
    //Patient
    private double pShunt;
    private double pDeadSpace;
    private double pVO2;
    private double pVCO2;
    private double pCardiacOutput;
    private double pHGB;
    private double pTemperature;
    private double pBarPressure;
    private double pWeight;
    private double pCompliance;
    private double pResistance;
    
    //Runtime
    private double oTidalVolume;

    public void takeSnapshot(CustomRespirator respirator) {
        //Ventilator
        this.vFrequency = respirator.getvFrequency();
        this.vFIO2 = respirator.getvFIO2();
        this.vPEEP = respirator.getvPEEP();
        this.vPIP = respirator.getvPIP();
        this.vPauseTime = respirator.getvPauseTime();
        this.vFlowRate = respirator.getvFlowRate();
        this.vInspiratoryTime = respirator.getvInspiratoryTime();

        //Patient
        this.pShunt = respirator.getpShunt();
        this.pDeadSpace = respirator.getpDeadSpace();
        this.pVO2 = respirator.getpVO2();
        this.pVCO2 = respirator.getpVCO2();
        this.pCardiacOutput = respirator.getpCardiacOutput();
        this.pHGB = respirator.getpHGB();
        this.pTemperature = respirator.getpTemperature();
        this.pBarPressure = respirator.getpBarPressure();
        this.pWeight = respirator.getpWeight();
        this.pCompliance = respirator.getpCompliance();
        this.pResistance = respirator.getpResistance();

        //Runtime
        this.oTidalVolume = respirator.getoTidalVolume();
    }
    
    public void apply(CustomRespirator respirator){
        //Ventilator
        respirator.vFrequency.setDouble(this.vFrequency);
        respirator.vFIO2.setDouble(this.vFIO2);
        respirator.vPEEP.setDouble(this.vPEEP);
        respirator.vPIP.setDouble(this.vPIP);
        respirator.vPauseTime.setDouble(this.vPauseTime);
        respirator.vFlowRate.setDouble(this.vFlowRate);
        respirator.vInspiratoryTime.setDouble(this.vInspiratoryTime);
        
        //Patient
        respirator.pShunt.setDouble(this.pShunt);
        respirator.pDeadSpace.setDouble(this.pDeadSpace);
        respirator.pVO2.setDouble(this.pVO2);
        respirator.pVCO2.setDouble(this.pVCO2);
        respirator.pCardiacOutput.setDouble(this.pCardiacOutput);
        respirator.pHGB.setDouble(this.pHGB);
        respirator.pTemperature.setDouble(this.pTemperature);
        respirator.pBarPressure.setDouble(this.pBarPressure);
        respirator.pWeight.setDouble(this.pWeight);
        respirator.pCompliance.setDouble(this.pCompliance);
        respirator.pResistance.setDouble(this.pResistance);
        
        
        //Runtime
        respirator.oTidalVolume.set(this.oTidalVolume);
        
        
    }

    public double getvFrequency() {
        return vFrequency;
    }

    public double getvFIO2() {
        return vFIO2;
    }

    public double getvPEEP() {
        return vPEEP;
    }

    public double getvPIP() {
        return vPIP;
    }

    public double getvPauseTime() {
        return vPauseTime;
    }

    public double getvFlowRate() {
        return vFlowRate;
    }

    public double getvInspiratoryTime() {
        return vInspiratoryTime;
    }

    public double getpShunt() {
        return pShunt;
    }

    public double getpDeadSpace() {
        return pDeadSpace;
    }
    
    public double getpVO2() {
        return pVO2;
    }

    public double getpVCO2() {
        return pVCO2;
    }

    public double getpCardiacOutput() {
        return pCardiacOutput;
    }

    public double getpHGB() {
        return pHGB;
    }

    public double getpTemperature() {
        return pTemperature;
    }

    public double getpBarPressure() {
        return pBarPressure;
    }

    public double getoTidalVolume() {
        return oTidalVolume;
    }

    public double getpWeight() {
        return pWeight;
    }

    public double getpCompliance() {
        return pCompliance;
    }

    public double getpResistance() {
        return pResistance;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.vFrequency) ^ (Double.doubleToLongBits(this.vFrequency) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.vFIO2) ^ (Double.doubleToLongBits(this.vFIO2) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.vPEEP) ^ (Double.doubleToLongBits(this.vPEEP) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.vPIP) ^ (Double.doubleToLongBits(this.vPIP) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.vPauseTime) ^ (Double.doubleToLongBits(this.vPauseTime) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.vFlowRate) ^ (Double.doubleToLongBits(this.vFlowRate) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.vInspiratoryTime) ^ (Double.doubleToLongBits(this.vInspiratoryTime) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.pShunt) ^ (Double.doubleToLongBits(this.pShunt) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.pDeadSpace) ^ (Double.doubleToLongBits(this.pDeadSpace) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.pVO2) ^ (Double.doubleToLongBits(this.pVO2) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.pVCO2) ^ (Double.doubleToLongBits(this.pVCO2) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.pCardiacOutput) ^ (Double.doubleToLongBits(this.pCardiacOutput) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.pHGB) ^ (Double.doubleToLongBits(this.pHGB) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.pTemperature) ^ (Double.doubleToLongBits(this.pTemperature) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.pBarPressure) ^ (Double.doubleToLongBits(this.pBarPressure) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.pWeight) ^ (Double.doubleToLongBits(this.pWeight) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.pCompliance) ^ (Double.doubleToLongBits(this.pCompliance) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.pResistance) ^ (Double.doubleToLongBits(this.pResistance) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final VO2CalculationDataSnaphot other = (VO2CalculationDataSnaphot) obj;
        if (Double.doubleToLongBits(this.vFrequency) != Double.doubleToLongBits(other.vFrequency)) {
            return false;
        }
        if (Double.doubleToLongBits(this.vFIO2) != Double.doubleToLongBits(other.vFIO2)) {
            return false;
        }
        if (Double.doubleToLongBits(this.vPEEP) != Double.doubleToLongBits(other.vPEEP)) {
            return false;
        }
        if (Double.doubleToLongBits(this.vPIP) != Double.doubleToLongBits(other.vPIP)) {
            return false;
        }
        if (Double.doubleToLongBits(this.vPauseTime) != Double.doubleToLongBits(other.vPauseTime)) {
            return false;
        }
        if (Double.doubleToLongBits(this.vFlowRate) != Double.doubleToLongBits(other.vFlowRate)) {
            return false;
        }
        if (Double.doubleToLongBits(this.vInspiratoryTime) != Double.doubleToLongBits(other.vInspiratoryTime)) {
            return false;
        }
        if (Double.doubleToLongBits(this.pShunt) != Double.doubleToLongBits(other.pShunt)) {
            return false;
        }
        if (Double.doubleToLongBits(this.pDeadSpace) != Double.doubleToLongBits(other.pDeadSpace)) {
            return false;
        }
        if (Double.doubleToLongBits(this.pVO2) != Double.doubleToLongBits(other.pVO2)) {
            return false;
        }
        if (Double.doubleToLongBits(this.pVCO2) != Double.doubleToLongBits(other.pVCO2)) {
            return false;
        }
        if (Double.doubleToLongBits(this.pCardiacOutput) != Double.doubleToLongBits(other.pCardiacOutput)) {
            return false;
        }
        if (Double.doubleToLongBits(this.pHGB) != Double.doubleToLongBits(other.pHGB)) {
            return false;
        }
        if (Double.doubleToLongBits(this.pTemperature) != Double.doubleToLongBits(other.pTemperature)) {
            return false;
        }
        if (Double.doubleToLongBits(this.pBarPressure) != Double.doubleToLongBits(other.pBarPressure)) {
            return false;
        }
        if (Double.doubleToLongBits(this.pWeight) != Double.doubleToLongBits(other.pWeight)) {
            return false;
        }
        if (Double.doubleToLongBits(this.pCompliance) != Double.doubleToLongBits(other.pCompliance)) {
            return false;
        }
        if (Double.doubleToLongBits(this.pResistance) != Double.doubleToLongBits(other.pResistance)) {
            return false;
        }
        return true;
    }

    
    
}
