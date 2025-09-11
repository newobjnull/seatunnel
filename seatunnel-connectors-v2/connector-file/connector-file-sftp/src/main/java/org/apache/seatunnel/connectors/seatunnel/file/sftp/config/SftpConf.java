/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.seatunnel.connectors.seatunnel.file.sftp.config;

import org.apache.seatunnel.api.configuration.ReadonlyConfig;
import org.apache.seatunnel.connectors.seatunnel.file.config.HadoopConf;
import org.apache.seatunnel.connectors.seatunnel.file.sftp.system.SFTPFileSystem;

import java.util.Base64;
import java.util.HashMap;
import java.util.Optional;

public class SftpConf extends HadoopConf {
    private static final String HDFS_IMPL =
            "org.apache.seatunnel.connectors.seatunnel.file.sftp.system.SFTPFileSystem";
    private static final String SCHEMA = "sftp";

    private SftpConf(String hdfsNameKey) {
        super(hdfsNameKey);
    }

    @Override
    public String getFsHdfsImpl() {
        return HDFS_IMPL;
    }

    @Override
    public String getSchema() {
        return SCHEMA;
    }

    public static HadoopConf buildWithConfig(ReadonlyConfig config) {
        String host = config.get(SftpFileBaseOptions.SFTP_HOST);
        int port = config.get(SftpFileBaseOptions.SFTP_PORT);
        String defaultFS = String.format("sftp://%s:%s", host, port);
        HadoopConf hadoopConf = new SftpConf(defaultFS);
        HashMap<String, String> sftpOptions = new HashMap<>();
        sftpOptions.put(
                SFTPFileSystem.FS_SFTP_USER_PREFIX + host,
                config.get(SftpFileBaseOptions.SFTP_USER));

        Optional<String> passwordOptional = config.getOptional(SftpFileBaseOptions.SFTP_PASSWORD);
        if (passwordOptional.isPresent() && passwordOptional.get().length() > 0) {
            sftpOptions.put(
                    SFTPFileSystem.FS_SFTP_PASSWORD_PREFIX
                            + host
                            + "."
                            + config.get(SftpFileBaseOptions.SFTP_USER),
                    passwordOptional.get());
        }
        Optional<String> keyFileOptional = config.getOptional(SftpFileBaseOptions.SFTP_KEYFILE);
        if (keyFileOptional.isPresent() && keyFileOptional.get().length() > 0) {
            sftpOptions.put(
                    SFTPFileSystem.FS_SFTP_KEYFILE_PREFIX
                            + host
                            + "."
                            + config.get(SftpFileBaseOptions.SFTP_USER),
                    new String(Base64.getDecoder().decode(keyFileOptional.get())));
        }
        Optional<String> keyFilePassPhraseOptional =
                config.getOptional(SftpFileBaseOptions.SFTP_KEYFILE_PASSPHRASE);
        if (keyFilePassPhraseOptional.isPresent() && keyFilePassPhraseOptional.get().length() > 0) {
            sftpOptions.put(
                    SFTPFileSystem.FS_SFTP_KEYFILE_PHRASE_PREFIX
                            + host
                            + "."
                            + config.get(SftpFileBaseOptions.SFTP_USER),
                    keyFilePassPhraseOptional.get());
        }

        hadoopConf.setExtraOptions(sftpOptions);
        return hadoopConf;
    }
}
