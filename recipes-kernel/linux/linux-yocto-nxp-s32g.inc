#
# Copyright (C) 2020 Wind River Systems, Inc.
#

FILESEXTRAPATHS:prepend:nxp-s32g := "${THISDIR}/files:"
SRC_URI:append:nxp-s32g = " ${@bb.utils.contains('DISTRO_FEATURES', 'dm-verity', 'file://0001-crypto-hse-Drop-the-sha256-support.patch', '', d)}"
COMPATIBLE_MACHINE:nxp-s32g = "nxp-s32g"
TARGET_SUPPORTED_KTYPES:nxp-s32g = "standard preempt-rt"

