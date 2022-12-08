#This is a inclued file for pfeng MASTER/SLAVE drivers

inherit module deploy
require pfe_common.inc

# Provide a default so things will parse.
#
PFE_LOCAL_FIRMWARE_DIR ??= "."

SRC_URI:append = " \
        file://0001-pfe_compiler-add-GCC-version-10.2.0-support.patch \
        file://0001-fix-hwts-kmemleak.patch \
        file://0001-pfe-sw-drop-the-unneeded-codes-to-fix-build-errors.patch \
        file://0001-pfe-netif-use-the-correct-ndo_eth_ioctl-to-fix-the-p.patch \
        file://0001-pfe-set-mac_managed_pm-to-true-to-fix-mdio-resume-wa.patch \
        file://0001-pfe-fix-the-lock-inconsistent-warning-when-rmmod-the.patch \
        file://0001-pfe-update-to-fix-the-possible-circular-locking.patch \
        file://0001-pfe-fix-the-kernel-calltrace-when-dhcp-for-pfe-ports.patch \
        file://${PFE_LOCAL_FIRMWARE_DIR}\
"

S = "${WORKDIR}/git"
MDIR = "${S}/sw/linux-pfeng"
INSTALL_DIR = "${D}/${nonarch_base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/net/ethernet/nxp/pfe"

EXTRA_OEMAKE:append = " KERNELDIR=${STAGING_KERNEL_DIR} MDIR=${MDIR} -C ${MDIR} V=1 drv-build"