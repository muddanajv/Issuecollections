package org.jfrog.hudson.pipeline.common.executors;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jfrog.build.extractor.clientConfiguration.ArtifactoryClientConfiguration;
import org.jfrog.hudson.pipeline.common.types.buildInfo.BuildInfo;
import org.jfrog.hudson.pipeline.common.types.deployers.Deployer;
import org.jfrog.hudson.pipeline.common.types.resolvers.Resolver;

/**
 * @author yahavi
 */
public class NpmEnvExtractor extends EnvExtractor {
    private String npmExe;
    private String args;
    private String path;
    private String module;

    public NpmEnvExtractor(Run build, BuildInfo buildInfo, Deployer deployer, Resolver resolver,
                           TaskListener buildListener, Launcher launcher, FilePath tempDir,
                           EnvVars env, String args, String path, String npmExe, String module) {
        super(build, buildInfo, deployer, resolver, buildListener, launcher, tempDir, env);
        this.npmExe = npmExe;
        this.args = args;
        this.path = path;
        this.module = module;
    }

    @Override
    protected void addExtraConfiguration(ArtifactoryClientConfiguration configuration) {
        configuration.npmHandler.setNpmExecutablePath(npmExe);
        configuration.npmHandler.setNpmInstallArgs(args);
        configuration.npmHandler.setNpmPath(path);
        configuration.npmHandler.setNpmModule(module);
    }
}
