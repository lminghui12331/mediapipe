// Copyright 2023 The MediaPipe Authors. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.mediapipe.tasks.vision.poselandmarker;

import com.google.auto.value.AutoValue;
import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.framework.image.MPImage;
import com.google.mediapipe.tasks.components.containers.Landmark;
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark;
import com.google.mediapipe.tasks.core.TaskResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/** Represents the pose landmarks deection results generated by {@link PoseLandmarker}. */
@AutoValue
public abstract class PoseLandmarkerResult implements TaskResult {

  /**
   * Creates a {@link PoseLandmarkerResult} instance from the lists of landmarks and
   * segmentationMask protobuf messages.
   *
   * @param landmarksProto a List of {@link NormalizedLandmarkList}
   * @param worldLandmarksProto a List of {@link LandmarkList}
   * @param segmentationMasksData a List of {@link MPImage}
   */
  static PoseLandmarkerResult create(
      List<LandmarkProto.NormalizedLandmarkList> landmarksProto,
      List<LandmarkProto.LandmarkList> worldLandmarksProto,
      List<LandmarkProto.NormalizedLandmarkList> auxiliaryLandmarksProto,
      Optional<List<MPImage>> segmentationMasksData,
      long timestampMs) {

    Optional<List<MPImage>> multiPoseSegmentationMasks = Optional.empty();
    if (segmentationMasksData.isPresent()) {
      multiPoseSegmentationMasks =
          Optional.of(Collections.unmodifiableList(segmentationMasksData.get()));
    }

    List<List<NormalizedLandmark>> multiPoseLandmarks = new ArrayList<>();
    List<List<Landmark>> multiPoseWorldLandmarks = new ArrayList<>();
    List<List<NormalizedLandmark>> multiPoseAuxiliaryLandmarks = new ArrayList<>();
    for (LandmarkProto.NormalizedLandmarkList poseLandmarksProto : landmarksProto) {
      List<NormalizedLandmark> poseLandmarks = new ArrayList<>();
      multiPoseLandmarks.add(poseLandmarks);
      for (LandmarkProto.NormalizedLandmark poseLandmarkProto :
          poseLandmarksProto.getLandmarkList()) {
        poseLandmarks.add(
            NormalizedLandmark.create(
                poseLandmarkProto.getX(), poseLandmarkProto.getY(), poseLandmarkProto.getZ()));
      }
    }
    for (LandmarkProto.LandmarkList poseWorldLandmarksProto : worldLandmarksProto) {
      List<Landmark> poseWorldLandmarks = new ArrayList<>();
      multiPoseWorldLandmarks.add(poseWorldLandmarks);
      for (LandmarkProto.Landmark poseWorldLandmarkProto :
          poseWorldLandmarksProto.getLandmarkList()) {
        poseWorldLandmarks.add(
            Landmark.create(
                poseWorldLandmarkProto.getX(),
                poseWorldLandmarkProto.getY(),
                poseWorldLandmarkProto.getZ()));
      }
    }
    for (LandmarkProto.NormalizedLandmarkList poseAuxiliaryLandmarksProto :
        auxiliaryLandmarksProto) {
      List<NormalizedLandmark> poseAuxiliaryLandmarks = new ArrayList<>();
      multiPoseAuxiliaryLandmarks.add(poseAuxiliaryLandmarks);
      for (LandmarkProto.NormalizedLandmark poseAuxiliaryLandmarkProto :
          poseAuxiliaryLandmarksProto.getLandmarkList()) {
        poseAuxiliaryLandmarks.add(
            NormalizedLandmark.create(
                poseAuxiliaryLandmarkProto.getX(),
                poseAuxiliaryLandmarkProto.getY(),
                poseAuxiliaryLandmarkProto.getZ()));
      }
    }
    return new AutoValue_PoseLandmarkerResult(
        timestampMs,
        Collections.unmodifiableList(multiPoseLandmarks),
        Collections.unmodifiableList(multiPoseWorldLandmarks),
        Collections.unmodifiableList(multiPoseAuxiliaryLandmarks),
        multiPoseSegmentationMasks);
  }

  @Override
  public abstract long timestampMs();

  /** Pose landmarks of detected poses. */
  public abstract List<List<NormalizedLandmark>> landmarks();

  /** Pose landmarks in world coordniates of detected poses. */
  public abstract List<List<Landmark>> worldLandmarks();

  /** Pose auxiliary landmarks. */
  public abstract List<List<NormalizedLandmark>> auxiliaryLandmarks();

  /** Pose segmentation masks. */
  public abstract Optional<List<MPImage>> segmentationMasks();
}
