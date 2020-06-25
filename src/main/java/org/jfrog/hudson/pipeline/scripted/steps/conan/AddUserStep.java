package org.jfrog.hudson.pipeline.scripted.steps.conan;

import com.google.inject.Inject;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.jfrog.hudson.CredentialsConfig;
import org.jfrog.hudson.pipeline.common.ArtifactoryConfigurator;
import org.jfrog.hudson.pipeline.common.Utils;
import org.jfrog.hudson.pipeline.common.executors.ConanExecutor;
import org.jfrog.hudson.pipeline.common.types.ArtifactoryServer;
import org.jfrog.hudson.util.CredentialManager;
import org.kohsuke.stapler.DataBoundConstructor;

public class AddUserStep extends AbstractStepImpl {
    private ArtifactoryServer server;
    private String serverName;
    private String conanHome;

    @DataBoundConstructor
    public AddUserStep(ArtifactoryServer server, String serverName, String conanHome) {
        this.server = server;
        this.serverName = serverName;
        this.conanHome = conanHome;
    }

    public ArtifactoryServer getServer() {
        return server;
    }

    public String getServerName() {
        return serverName;
    }

    public String getConanHome() {
        return conanHome;
    }

    public static class Execution extends AbstractSynchronousStepExecution<Boolean> {
        private static final long serialVersionUID = 1L;

        @StepContextParameter
        private transient Run build;

        @StepContextParameter
        private transient TaskListener listener;

        @StepContextParameter
        private transient Launcher launcher;

        @Inject(optional = true)
        private transient AddUserStep step;

        @StepContextParameter
        private transient FilePath ws;

        @StepContextParameter
        private transient EnvVars env;

        @Override
        protected Boolean run() throws Exception {
            org.jfrog.hudson.ArtifactoryServer artifactoryServer = Utils.prepareArtifactoryServer(null, step.getServer());
            ArtifactoryConfigurator configurator = new ArtifactoryConfigurator(artifactoryServer);
            CredentialsConfig deployerConfig = CredentialManager.getPreferredDeployer(configurator, artifactoryServer);
            String username = deployerConfig.provideCredentials(build.getParent()).getUsername();
            String password = deployerConfig.provideCredentials(build.getParent()).getPassword();
            String serverName = step.getServerName();
            ConanExecutor executor = new ConanExecutor(step.getConanHome(), ws, launcher, listener, env, build);
            executor.execUserAdd(username, password, serverName);
            return true;
        }
    }

    @Extension
    public static final class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(AddUserStep.Execution.class);
        }

        @Override
        public String getFunctionName() {
            return "conanAddUser";
        }

        @Override
        public String getDisplayName() {
            return "Add new user to Conan config";
        }

        @Override
        public boolean isAdvanced() {
            return true;
        }
    }
}