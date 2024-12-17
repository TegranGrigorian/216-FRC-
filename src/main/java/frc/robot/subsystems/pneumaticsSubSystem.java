package frc.robot.subsystems;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import frc.robot.Constants;

public class pneumaticsSubSystem {
    private final Solenoid armSolenoid = new Solenoid(
        Constants.Pneumatics.PCM_CAN_PORT, 
        PneumaticsModuleType.CTREPCM, 
        Constants.Pneumatics.ARM_SOLENOID_PORT
    );

    public void extendArm() {
        armSolenoid.set(true);
    }
}
