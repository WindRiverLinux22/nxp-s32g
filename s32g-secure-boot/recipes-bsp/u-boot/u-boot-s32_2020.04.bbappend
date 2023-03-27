SRC_URI:append = " \
    file://0001-configs-s32g2xx-enable-CONFIG_FIT_SIGNATURE-for-secu.patch \
    file://0001-arch-mach-s32-extend-the-DTB-size-for-BL33.patch \
    file://0001-Revert-hse-secboot-remove-unused-u-boot-secboot-code.patch \
    file://0002-u-boot-secboot-correct-the-secure-boot-config.patch \
    file://0003-s32-hse-support-secure-boot-feature-on-both-S32G2-an.patch \
"

python() {
    if d.getVar('HSE_SEC_ENABLED') == '0':
        bb.fatal("Please set HSE firmware path for secure boot feature firstly, and then build again.")
}
