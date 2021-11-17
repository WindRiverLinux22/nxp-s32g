DESCRIPTION = "NXP HSE PKCS#11 Module"
PROVIDES += "pkcs11-hse"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = " \
    file://README.md;md5=b451d36d865e4242aa2b944fb0370269 \
"

DEPENDS = "openssl libp11"

SRC_URI = "https://bitbucket.sw.nxp.com/projects/ALBW/repos/pkcs11-hse/pkcs11-hse.tar.gz"

SRCREV = "0fc8ecf67d7758ea395dd8bf8db87f787ca82fe8"
SRC_URI[sha256sum] = "6af5c7cc0593c3721fe18c0f657b64ec1c7ebebb2103ed6e97f71fd6dcc26bdd"

SRC_URI += " \
    file://bsp31/rc2/0001-pkcs-standalone-HSE-UIO-driver-support.patch \
    file://bsp31/rc3/0001-pkcs-update-makefile-and-folders.patch \
    file://bsp31/rc3/0002-pkcs-update-.gitingore.patch \
    file://bsp31/rc3/0003-pkcs-renamed-hse-usr.h-to-libhse.h.patch \
    file://bsp31/rc3/0004-pkcs-switch-gCtx-access-from-extern-to-getter.patch \
    file://bsp31/rc3/0005-pkcs-fix-fPIC-on-.o-files.patch \
    file://bsp31/rc4/0001-makefile-check-prerequisites.patch \
    file://bsp31/rc4/0002-libhse-define-low-level-driver-interface.patch \
    file://bsp31/rc4/0003-libhse-fix-register-definition-and-internal-struct.patch \
    file://bsp31/rc4/0004-libhse-enable-UIO-device-configuration-from-makefile.patch \
    file://bsp31/rc5/0001-pkcs-ddr-memory-allocator.patch \
    file://bsp31/rc5/0002-pkcs-refactor-code-to-use-hse-memory-allocator.patch \
    file://bsp31/rc5/0003-pkcs-conform-to-expected-interface-behaviour.patch \
    file://bsp31/rc5/0004-pkcs-add-rng-ops.patch \
    file://bsp31/rc7/0001-pkcs-fix-tokenInit-breaking-libp11-pkcs11-tool.patch \
    file://bsp31/rc7/0002-pkcs-update-makefile-for-hse-fw-1.0.0.patch \
    file://0001-pkcs11-hse-Makefile-using-internal-compile-variables.patch \
"

PATCHTOOL = "git"
PACKAGE_ARCH = "${MACHINE_ARCH}"

S = "${WORKDIR}/pkcs11-hse"

COMPATIBLE_MACHINE:nxp-s32g2xx = "nxp-s32g2xx"

do_compile() {
    sed -i 's/$(CROSS_COMPILE)gcc /$(CC) /' Makefile examples/Makefile
    oe_runmake HSE_FWDIR=${S}/hes-fw  CFLAGS="${CFLAGS} -shared -fPIC -Wall -fno-builtin"
    oe_runmake -C examples LIBS="-L${STAGING_LIBDIR}/" INCLUDE="-I${STAGING_INCDIR}" LDFLAGS="${LDFLAGS} -lcrypto -lp11"
}

do_install() {
    install -d ${D}${libdir}

    install -m 0755 ${S}/libpkcs-hse.so ${D}${libdir}/libpkcs-hse.so.1.0
    ln -s libpkcs-hse.so.1.0 ${D}${libdir}/libpkcs-hse.so
    install -m 0755 ${S}/libhse.so.1.0 ${D}${libdir}/libhse.so.1.0
    ln -s libhse.so.1.0 ${D}${libdir}/libhse.so.1

    install -d ${D}${includedir}
    install -m 0644 ${S}/libhse/*.h ${D}${includedir}
    install -m 0644 ${S}/libpkcs/*.h ${D}${includedir}

    install -d ${D}${bindir}
    install -m 0755 ${S}/examples/pkcs-keyop ${D}${bindir}
}

PACKAGES =+ "${PN}-examples "
FILES:${PN}-examples += "${bindir}"
