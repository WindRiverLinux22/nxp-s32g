python () {
    if d.getVar('ATF_SIGN_ENABLE') == '1':
        d.appendVar('IMAGE_BOOT_FILES', ' fitImage')
        if bb.utils.contains('DISTRO_FEATURES', 'dm-verity', True, False, d):
            if d.getVar('INITRAMFS_IMAGE_BUNDLE') != '1':
                d.appendVar('IMAGE_BOOT_FILES', ' fitImage-${INITRAMFS_IMAGE}-${MACHINE}-${MACHINE};fitImage')
    else:
        d.appendVar('IMAGE_BOOT_FILES', ' Image ${DTB_FILES}')
        if bb.utils.contains('DISTRO_FEATURES', 'dm-verity', True, False, d):
            if d.getVar('INITRAMFS_IMAGE_BUNDLE') == '1':
                d.appendVar('IMAGE_BOOT_FILES', ' Image-initramfs-${MACHINE}${KERNEL_IMAGE_BIN_EXT};Image')
            else:
                d.appendVar('IMAGE_BOOT_FILES', ' ${INITRAMFS_IMAGE}-${MACHINE}.cpio.gz.u-boot;initramfs')

}
