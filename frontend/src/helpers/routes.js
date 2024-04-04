import BaseBrowser from "../components/base/BaseBrowser";
import BaseModelProcessCore from "../components/base/BaseModelProcessCore";
import BaseDetails from "../components/base/BaseDetailsViewer.vue";
import DatasetEditor from "../components/evaluations/experiment/data/DatasetEditor";
import SampleDeviceEditor from "../components/evaluations/experiment/data/SampleDeviceEditor";
import BiometricSystemEditor from "../components/evaluations/experiment/biometricsystem/BiometricSystemEditor";
import StandaloneCriteriaEditor from "../components/evaluations/criteria/StandaloneCriteriaPicker.vue";
import constants from "./constants";
import DatasetBrowser from "../components/evaluations/experiment/data/DatasetBrowser";
import SampleDevicePicker from "../components/evaluations/experiment/data/SampleDevicePicker";
import BiometricSystemBrowser from "@/components/evaluations/experiment/biometricsystem/BiometricSystemBrowser";
import ArtifactReviewBrowser from "../components/review/ArtifactReviewBrowser.vue";

const routes = [
    { path: '/', component: BaseBrowser, props: {standAlone: true} },
    { path: '/browse/base', component: BaseBrowser, props: {standAlone: true} },
    { path: constants.routes.browseDatasets.path, component: DatasetBrowser, name: constants.routes.browseDatasets.name, props: true  },
    { path: constants.routes.browseCriteria.path, component: StandaloneCriteriaEditor, name: constants.routes.browseCriteria.name, props: true },
    { path: constants.routes.browseSampleDevices.path, component: SampleDevicePicker , name: constants.routes.browseSampleDevices.name, props: true },
    { path: constants.routes.browseBiometricSystems.path, component: BiometricSystemBrowser , name: constants.routes.browseBiometricSystems.name, props: true },
    { path: '/model/base', component: BaseModelProcessCore },
    { path: '/model/base/:name', component: BaseModelProcessCore, name: "continue-base-modelling", props: true },
    { path: constants.routes.modelDatasets.path, component: DatasetEditor , name: constants.routes.modelDatasets.name, props: true },
    { path: constants.routes.modelSampleDevices.path, component: SampleDeviceEditor , name: constants.routes.modelSampleDevices.name, props: true },
    { path: '/model/criteria', component: StandaloneCriteriaEditor},
    { path: '/details/base/:name', component: BaseDetails, name: "base-details", props: true },
    { path: '/details/biometricsystem/:id', component: BiometricSystemEditor, name: "biometric-system-details", props: true },
    { path: constants.routes.ownArtifactsInReview.path, component: ArtifactReviewBrowser, name: constants.routes.ownArtifactsInReview.name, props: true },
    { path: constants.routes.artifactsRequiringReview.path, component: ArtifactReviewBrowser, name: constants.routes.artifactsRequiringReview.name, props: true },
];

export default routes;