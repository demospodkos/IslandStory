using UnityEditor;
using UnityEditor.Build.Reporting;
public static class BuildScript {
  public static void BuildAndroid() {
    var report = BuildPipeline.BuildPlayer(new BuildPlayerOptions {
      scenes = new[] { "Assets/Scenes/Main.unity" },
      locationPathName = "build/IslandStory.apk",
      target = BuildTarget.Android,
      options = BuildOptions.None
    });
    if (report.summary.result != BuildResult.Succeeded)
      throw new System.Exception("Android build failed: " + report.summary.result);
  }
}
