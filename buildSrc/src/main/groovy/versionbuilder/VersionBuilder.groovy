package versionbuilder

class VersionBuilder {
    static final int GIT_COMMIT_COUNT_NORMALIZE = 2320;
    static final int GIT_COMMIT_COUNT_MINOR_NORMALIZE = 140+50+38;

    static def buildGitVersionNumber() {
        int  versionNumber=Integer.parseInt('git rev-list --count HEAD'.execute().text.trim()) - GIT_COMMIT_COUNT_NORMALIZE;
        println("versionNumber="+versionNumber);
        return versionNumber;
    }

    static def buildGitVersionName() {
        String versionName=String.format("%d.%d.%d", 1, 0, buildGitVersionNumber() - GIT_COMMIT_COUNT_MINOR_NORMALIZE);
        println("version Name="+versionName);
        return versionName;
    }

}