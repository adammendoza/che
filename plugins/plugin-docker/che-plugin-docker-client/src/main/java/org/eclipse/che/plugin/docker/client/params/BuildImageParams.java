/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.plugin.docker.client.params;

import org.eclipse.che.plugin.docker.client.ProgressMonitor;
import org.eclipse.che.plugin.docker.client.dto.AuthConfigs;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.requireNonNull;
import static org.eclipse.che.plugin.docker.client.params.ParamsUtils.requireNonEmptyArray;
import static org.eclipse.che.plugin.docker.client.params.ParamsUtils.requireNonNullNorEmpty;

/**
 * Arguments holder for {@link org.eclipse.che.plugin.docker.client.DockerConnector#buildImage(BuildImageParams, ProgressMonitor)}.
 *
 * @author Mykola Morhun
 * @author Alexander Garagatyi
 */
public class BuildImageParams {
    private String             repository;
    private String             tag;
    private AuthConfigs        authConfigs;
    private Boolean            doForcePull;
    private Long               memoryLimit;
    private Long               memorySwapLimit;
    private List<File>         files;
    private String             dockerfile;
    private String             remote;
    private Boolean            q;
    private Boolean            nocache;
    private Boolean            rm;
    private Boolean            forceRm;
    private Map<String,String> buildArgs;

    /**
     * Creates arguments holder with required parameters.
     *
     * @param files
     *         info about this parameter see {@link #withFiles(File...)}
     * @return arguments holder with required parameters
     * @throws NullPointerException
     *         if {@code files} is null
     * @throws IllegalArgumentException
     *         if {@code files} is empty array
     *
     */
    public static BuildImageParams create(@NotNull File... files) {
        return new BuildImageParams().withFiles(files);
    }

    /**
     * Creates arguments holder with required parameters.
     *
     * @param remote
     *         info about this parameter see {@link #withRemote(String)}
     * @return arguments holder with required parameters
     * @throws NullPointerException
     *         if {@code remote} is null
     * @throws IllegalArgumentException
     *         if {@code remote} is empty
     */
    public static BuildImageParams create(@NotNull String remote) {
        return new BuildImageParams().withRemote(remote);
    }

    private BuildImageParams() {}

    /**
     * Adds repository to this parameters.
     *
     * @param repository
     *         full repository name to be applied to newly created image
     * @return this params instance
     */
    public BuildImageParams withRepository(String repository) {
        this.repository = repository;
        return this;
    }

    /**
     * Adds tag to this parameters.
     *
     * @param tag
     *         tag of the image
     * @return this params instance
     */
    public BuildImageParams withTag(String tag) {
        this.tag = tag;
        return this;
    }

    /**
     * Adds auth configuration to this parameters.
     *
     * @param authConfigs
     *         authentication configuration for registries
     * @return this params instance
     */
    public BuildImageParams withAuthConfigs(AuthConfigs authConfigs) {
        this.authConfigs = authConfigs;
        return this;
    }

    /**
     * Adds force flag to this parameters.
     *
     * @param doForcePull
     *         if {@code true} attempts to pull the image even if an older image exists locally
     * @return this params instance
     */
    public BuildImageParams withDoForcePull(boolean doForcePull) {
        this.doForcePull = doForcePull;
        return this;
    }

    /**
     * Adds RAM memory limit to this parameters.
     *
     * @param memoryLimit
     *         RAM memory limit for build in bytes
     * @return this params instance
     */
    public BuildImageParams withMemoryLimit(long memoryLimit) {
        this.memoryLimit = memoryLimit;
        return this;
    }

    /**
     * Adds swap memory limit to this parameters.
     *
     * @param memorySwapLimit
     *         total memory in bytes (memory + swap), -1 to enable unlimited swap
     * @return this params instance
     */
    public BuildImageParams withMemorySwapLimit(long memorySwapLimit) {
        this.memorySwapLimit = memorySwapLimit;
        return this;
    }

    /**
     * Sets list of files for creation docker image.
     * One of them must be Dockerfile.
     *
     * @param files
     *         files that are needed for creation docker images (e.g. file of directories used in ADD instruction in Dockerfile).
     *         One of them must be Dockerfile.
     * @return this params instance
     * @throws NullPointerException
     *         if {@code files} is null
     * @throws IllegalArgumentException
     *         if {@code files} is empty array
     * @throws IllegalStateException
     *         if other parameter incompatible with files is set
     */
    public BuildImageParams withFiles(@NotNull File... files) {
        if (remote != null) {
            throw new IllegalStateException("Remote parameter is already set. Remote and files parameters are mutually exclusive.");
        }
        requireNonNull(files);
        requireNonEmptyArray(files);
        this.files = new ArrayList<>(files.length + 1);
        return addFiles(files);
    }

    /**
     * Adds files to the file list.
     * see {@link #withFiles(File...)}
     *
     * @param files
     *         files to add to image
     * @return this params instance
     * @throws NullPointerException
     *         if {@code files} is null
     */
    public BuildImageParams addFiles(@NotNull File... files) {
        requireNonNull(files);
        for (File file : files) {
            requireNonNull(file);
            this.files.add(file);
        }
        return this;
    }

    /**
     * Sets GIT repo or HTTP(S) location of dockerfile or build sources
     *
     * @param remote
     *         URI of build context
     * @return this params instance
     * @throws NullPointerException
     *         if {@code remote} is null
     * @throws IllegalArgumentException
     *         if {@code remote} is empty
     * @throws IllegalStateException
     *         if other parameter incompatible with remote is set
     */
    public BuildImageParams withRemote(@NotNull String remote) {
        requireNonNullNorEmpty(remote);
        if (files != null) {
            throw new IllegalStateException("Files parameter is already set. Remote and files parameters are mutually exclusive.");
        }

        this.remote = remote;
        return this;
    }

    /**
     * Suppress verbose build output.
     *
     * @param q
     *         quiet flag
     * @return this params instance
     */
    public BuildImageParams withQ(boolean q) {
        this.q = q;
        return this;
    }

    /**
     * Do not use the cache when building the image.
     *
     * @param nocache
     *         nocache flag
     * @return this params instance
     */
    public BuildImageParams withNocache(boolean nocache) {
        this.nocache = nocache;
        return this;
    }

    /**
     * Remove intermediate containers after a successful build.
     *
     * @param rm
     *         rm flag
     * @return this params instance
     */
    public BuildImageParams withRm(boolean rm) {
        this.rm = rm;
        return this;
    }

    /**
     * Always remove intermediate containers (includes rm).
     *
     * @param forceRm
     *         forceRm flag
     * @return this params instance
     */
    public BuildImageParams withForceRm(boolean forceRm) {
        this.forceRm = forceRm;
        return this;
    }

    /**
     * JSON map of string pairs for build-time variables.
     * Users pass these values at build-time.
     * Docker uses the buildargs as the environment context for command(s) run via the Dockerfile’s RUN instruction
     * or for variable expansion in other Dockerfile instructions.
     *
     * @param buildArgs
     *         map of build arguments
     * @return this params instance
     */
    public BuildImageParams withBuildArgs(Map<String,String> buildArgs) {
        this.buildArgs = buildArgs;
        return this;
    }

    /**
     * Adds build arg to this parameters object.
     *
     * @param key
     *         build arg key
     * @param value
     *         build arg value
     * @return this params instance
     * @throws NullPointerException
     *         if {@code key} is null
     */
    public BuildImageParams addBuildArg(@NotNull String key, String value) {
        requireNonNull(key);

        if (buildArgs == null) {
            buildArgs = new HashMap<>();
        }

        buildArgs.put(key, value);
        return this;
    }

    /**
     * Sets path to alternate dockerfile in build context
     *
     * @param dockerfilePath
     *         path of alternate dockerfile
     * @return this params instance
     */
    public BuildImageParams withDockerfile(String dockerfilePath) {
        this.dockerfile = dockerfilePath;
        return this;
    }

    public String getRepository() {
        return repository;
    }

    public String getTag() {
        return tag;
    }

    public AuthConfigs getAuthConfigs() {
        return authConfigs;
    }

    public Boolean isDoForcePull() {
        return doForcePull;
    }

    public Long getMemoryLimit() {
        return memoryLimit;
    }

    public Long getMemorySwapLimit() {
        return memorySwapLimit;
    }

    public List<File> getFiles() {
        return files;
    }

    public String getDockerfile() {
        return dockerfile;
    }

    public String getRemote() {
        return remote;
    }

    public Boolean isQ() {
        return q;
    }

    public Boolean isNocache() {
        return nocache;
    }

    public Boolean isRm() {
        return rm;
    }

    public Boolean isForcerm() {
        return forceRm;
    }

    public Map<String, String> getBuildargs() {
        return buildArgs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BuildImageParams that = (BuildImageParams)o;
        return Objects.equals(repository, that.repository) &&
               Objects.equals(tag, that.tag) &&
               Objects.equals(authConfigs, that.authConfigs) &&
               Objects.equals(doForcePull, that.doForcePull) &&
               Objects.equals(memoryLimit, that.memoryLimit) &&
               Objects.equals(memorySwapLimit, that.memorySwapLimit) &&
               Objects.equals(files, that.files) &&
               Objects.equals(dockerfile, that.dockerfile) &&
               Objects.equals(remote, that.remote) &&
               Objects.equals(q, that.q) &&
               Objects.equals(nocache, that.nocache) &&
               Objects.equals(rm, that.rm) &&
               Objects.equals(forceRm, that.forceRm) &&
               Objects.equals(buildArgs, that.buildArgs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(repository, tag, authConfigs, doForcePull, memoryLimit, memorySwapLimit, files, dockerfile, remote, q, nocache,
                            rm, forceRm, buildArgs);
    }

    @Override
    public String toString() {
        return "BuildImageParams{" +
               "repository='" + repository + '\'' +
               ", tag='" + tag + '\'' +
               ", authConfigs=" + authConfigs +
               ", doForcePull=" + doForcePull +
               ", memoryLimit=" + memoryLimit +
               ", memorySwapLimit=" + memorySwapLimit +
               ", files=" + files +
               ", dockerfile='" + dockerfile + '\'' +
               ", remote='" + remote + '\'' +
               ", q=" + q +
               ", nocache=" + nocache +
               ", rm=" + rm +
               ", forcerm=" + forceRm +
               ", buildargs=" + buildArgs +
               '}';
    }

}
