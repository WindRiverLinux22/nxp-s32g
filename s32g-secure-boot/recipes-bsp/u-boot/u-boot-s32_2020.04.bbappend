SRC_URI:append = " \
    file://0001-configs-s32g2xx-enable-CONFIG_FIT_SIGNATURE-for-secu.patch \
    file://0001-u-boot-32-Enable-support-for-the-legacy-image-format.patch \
    file://0001-arch-mach-s32-extend-the-DTB-size-for-BL33.patch \
    file://0001-Revert-hse-secboot-remove-unused-u-boot-secboot-code.patch \
    file://0002-u-boot-secboot-correct-the-secure-boot-config.patch \
    file://0003-s32-hse-support-secure-boot-feature-on-both-S32G2-an.patch \
    file://0001-s32g-hse-reconstruct-the-code-used-to-enable-secure-.patch \
    file://0001-s32g-hse-support-M7-secure-boot-feature.patch \
    file://0001-s32g-hse-support-NXP-parallel-secure-boot-feature.patch \
    file://0001-s32g-hse-support-Aptiv-autosar-secure-boot-feature.patch \
    file://0001-s32g-hse-support-Aptiv-parallel-secure-boot-feature.patch \
    file://0001-s32g-hse-improve-the-code-of-enable-secure-boot-comm.patch \
"

python() {
    if d.getVar('HSE_SEC_ENABLED') == '0':
        bb.fatal("Please set HSE firmware path for secure boot feature firstly, and then build again.")
}
