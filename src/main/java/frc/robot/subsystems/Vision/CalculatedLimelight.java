// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.Vision;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.robot.Constants.visionConstants;

/** Converts most of the important NetworkTables values to more friendly formats. */
public class CalculatedLimelight {

  ///// Values \\\\\
  NetworkTableInstance inst = NetworkTableInstance.getDefault();
  String key;



  /** Initializes a CalculatedLimelight based on the given camera name. */
  public CalculatedLimelight(String key) {
    this.key = key;
  }



  /** Returns the name of the camera. */
  public String getKey() {
    return key;
  }
  
  /** Returns the Limelight NetworkTable for this camera. */
  public NetworkTable getNetworkTable() {
    return inst.getTable(key);
  }

  /** Returns true if there is a target in view. */
  public boolean hasTarget() {
    return getNetworkTable().getEntry("tid").getInteger(-1) >= 0;
  }

  /** Returns the ID of the currently visible target, or -1 if none are present. */
  public long getTargetID() {
    return getNetworkTable().getEntry("tid").getInteger(-1);
  }

  /** Returns a value of how accurate we think the camera is at a given moment (higher value = more accurate). */
  public double getTrust() {
    double[] targetRelative = getNetworkTable().getEntry("botpose_targetspace").getDoubleArray(new double[7]);

    return (1 / Math.sqrt((targetRelative[0] * targetRelative[0]) + (targetRelative[2] * targetRelative[2]) + (visionConstants.AngleDistrust * Math.sin(targetRelative[4] * (Math.PI / 180)))));
  }

  /** Returns the field-relative Pose2d. */
  public Pose2d getFieldPose() {
    double[] fieldTable = getNetworkTable().getEntry("botpose_wpiblue").getDoubleArray(new double[7]);

    return new Pose2d(
      new Translation2d(
        fieldTable[0], // X position (right/left from camera perspective)
        fieldTable[2]  // Z position (forward/back from camera perspective)
      ),
      Rotation2d.fromDegrees(fieldTable[5]) // Rotation (pitch)
    );
  }

  /** Returns the target-relative Pose2d. */
  public Pose2d getTargetPose() {
    double[] fieldTable = getNetworkTable().getEntry("botpose_targetspace").getDoubleArray(new double[7]);

    return new Pose2d(
      new Translation2d(
        fieldTable[0], // X position (right/left from camera perspective)
        fieldTable[2]  // Z position (forward/back from camera perspective)
      ),
      Rotation2d.fromDegrees(fieldTable[5]) // Rotation (pitch)
    );
  }
}
