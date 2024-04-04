<template>
    <div>
        <v-dialog v-model="architectureInfoDialog" persistent>
            <template v-slot:activator="{ on }">
                <v-btn block v-on="on" color="red accent-1" text>Target Architecture?!</v-btn>
            </template>
            <v-card>
                <v-card-title>
                    <span class="headline">Overview: Architecture of Biometric Authentication Systems</span>
                </v-card-title>
                <v-card-text>
                    <v-container grid-list-md>
                        <v-row wrap>
                            <v-col cols="12" lg="12">
                                <span>Biometric authentication systems are usually built with the help of biometric systems that include machine learning systems
                                    solving the following tasks:</span>
                                <br><br>
                                <ul>
                                    <li><b>Data Capturing:</b>&nbsp;Human characteristics is sampled via sensors or APIs, e.g., using cameras, accelerometers or fingerprint sensors.</li>
                                    <li><b>Signal Processing</b>:&nbsp;The acquired signals are processed to apply machine learning. This can be traditional preparations for feature extraction for
                                        shallow machine learning but also some specific aggregation to fit deep learning systems. Acquired signals could be quality controlled, aggregated
                                        over a certain time, transformed into other domains (e.g. using Fourier Transformation), or be the basis to calculate features
                                        such as min, max or mean values, for example</li>
                                    <li><b>Template Creation:</b>:&nbsp;A machine learning technique or neural network creates signatures of specific users during the <i>enrollment</i>
                                        phase</li>
                                    <li><b>Data Storage:</b>&nbsp;The created signatures are stored for later queries or updates</li>
                                    <li><b>Matching:</b>&nbsp;In the matching phase, freshly acquired samples are processed similarly and matched to the stored signatures and depending on the <b>matching mode</b> of the system
                                        a decision is made. If the system runs in <i>identification</i> mode it reports which identity matches to the new samples to what extent or if it is an
                                        unknown user. In <i>verification</i> mode it reports whether the new samples matched a claimed identity that was enrolled earlier.</li>
                                    <li><b>Decision:</b>Finally, the matching results is used for a final decision to either <b>protect</b> a resource <b>explicitly</b> such as allowing or denying to log in to your operating system or to <b>provide an indicator</b> whether a claimed
                                        identity is really existent. In the latter case protection is <b>implicit</b> since the consumer of the indicator is in control and can decide whether access is granted or not.
                                        Implicit protection can be provided by <i>Trust Levels</i> or <i>Confidence Scores</i>, for example.</li>
                                </ul>
                                <br>
                                <span>Depending on the specific system's architecture each task will be handled by one or more components that may be deployed on different types of devices.
                                These <b><i>device categories</i></b> are determining the overall architecture and thus features of the overall system in terms of <i>usability</i>,
                                <i>security</i>, and <i>deployability</i>.</span>
                                <br><br>
                                <h3>Example</h3>
                                <br>
                                <v-data-table
                                        :headers="deploymentCategoriesExamples.headers"
                                        :items="deploymentCategoriesExamples.items"
                                        class="elevation-1"
                                        hide-default-footer
                                        disable-initial-sort
                                >
                                    <template slot="items" slot-scope="props">
                                        <td><b>{{ props.item.task }}</b></td>
                                        <td><span v-html="props.item.activity"></span></td>
                                        <td>{{ props.item.deviceCategory }}</td>
                                    </template>
                                </v-data-table>
                            </v-col>
                        </v-row>
                    </v-container>
                </v-card-text>
                <v-card-actions>
                    <v-btn color="primary" text @click="architectureInfoDialog = false">Got That</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </div>
</template>

<script>
    export default {
        name: "ArchitectureInfo",
        data() {
            return {
                architectureInfoDialog: false,
                deploymentCategoriesExamples : {
                    headers: [
                        { text: "Task", value: "task" },
                        { text: "Activity", value: "activity" },
                        { text: "Device Category", value: "deviceCategory" },
                    ],
                    items: [
                        {
                            task: "Signal Aquisition",
                            deviceCategory: "Smartphone",
                            activity: "Sampling built-in accelerometer sensorToEdit's data"
                        },
                        {
                            task: "Pre-Processing I",
                            deviceCategory: "Smartphone",
                            activity: "Applying windowing to X, Y dimension of raw sensorToEdit data and feature extraction (min, max) per window"
                        },
                        {
                            task: "Pre-Processing II",
                            deviceCategory: "Server",
                            activity: "Applying Fourier transformation to X dimension of data and enrich feature vector"
                        },
                        {
                            task: "Template Creation (Enrollment)",
                            deviceCategory: "Server",
                            activity: "Creating initial signature of individual using a Support Vector Machine (Enrollment)"
                        },
                        {
                            task: "Signature Storage",
                            deviceCategory: "Smartphone",
                            activity: "Storage of created signature for later verification"
                        },
                        {
                            task: "Matching",
                            deviceCategory: "Smartphone",
                            activity: "Matching new incoming data to stored signature and verify whether claimed identity is still in hold of smartphone"+
                                '<br><span class="font-weight-light">Matching mode:</span> Verification'
                        },
                        {
                            task: "Decision",
                            deviceCategory: "Smartphone",
                            activity: "Use the matching result do finally decide on access denial or grant"+
                                '&nbsp;<span class="font-weight-light">Protection mode:</span></h5> Explicit'+
                                '&nbsp;<span class="font-weight-light">Protected resource:</span></h5> Access to Smartphone Operating System'
                        }
                    ]
                }
            }
        }
    }
</script>

<style scoped>

</style>