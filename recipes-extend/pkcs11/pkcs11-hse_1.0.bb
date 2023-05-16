DESCRIPTION = "NXP HSE PKCS#11 Module"
PROVIDES += "pkcs11-hse"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = " \
    file://README.md;md5=b451d36d865e4242aa2b944fb0370269 \
"

DEPENDS = "openssl libp11"
RDEPENDS:${PN} = "opensc pcsc-lite ccid"

URL ?= "git://github.com/nxp-auto-linux/pkcs11-hse.git;protocol=https"
BRANCH ?= "release/bsp36.0"
SRC_URI = "${URL};branch=${BRANCH}"

SRCREV = "f04e9e5a6c8ea3bf5962b5665b393fb89ebc31b8"
SRC_URI[sha256sum] = "b529fcbbb8f4347310d433162b81291da5955f9916d5c6ad5f4dc316ef6aef14"

SRC_URI += " \
    file://0001-pkcs11-hse-Makefile-using-internal-compile-variables.patch \
    file://0001-hse-initialize-used-field-of-struct-node_data.patch \
    file://0001-hse-pkcs-secboot-replace-memcpy-with-specific-hse_me.patch \
    file://0001-Makefile-Make-examples-depend-on-libhse.so-to-fix-a-.patch \
"

PATCHTOOL = "git"
PACKAGE_ARCH = "${MACHINE_ARCH}"

S = "${WORKDIR}/git"

# Disable -O2 optimization, since it seems to be exposing an alignment issue
SELECTED_OPTIMIZATION:remove = "-O2"

EXTRA_OEMAKE += " \
	CROSS_COMPILE=${TARGET_PREFIX} \
"

CFLAGS += "${HOST_CC_ARCH}${TOOLCHAIN_OPTIONS}"


PKCS_DEMO_BINS = "pkcs-keyop hse-encrypt hse-sysimg pkcs-key-provision hse-secboot \
                  pkcs-cipher pkcs-msg-digest pkcs-sig"

do_compile() {


    plats="s32g2 s32g3"
    for plat in $plats; do

        mkdir -p ${S}/hse-fw/${plat}
        if [ "$plat" = "s32g2" ]; then
            fw_version="${HSE_FW_VERSION_S32G2}"
        else
            fw_version="${HSE_FW_VERSION_S32G3}"
        fi

        cp -r ${HSE_LOCAL_FIRMWARE_DIR}/${fw_version}/interface ${S}/hse-fw/${plat}/
        # compile share libraries(libhse and libpkcs) firstly, they are all same either S32G2 or S32G3
        oe_runmake HSE_FWDIR=${S}/hse-fw/${plat}  CFLAGS="${CFLAGS} -shared -fPIC -Wall -fno-builtin"

        # clean the example binaries because they are needed to be compiled with different options
        oe_runmake -C examples clean
        # compile demo apps which may not be same between S32G2 and S32G3
        oe_runmake -C examples HSE_FWDIR=${S}/hse-fw/${plat} PKCS11HSE_DIR=${S} LIBS="-L${STAGING_LIBDIR}/" INCLUDE="-I${STAGING_INCDIR}" LDFLAGS="${LDFLAGS} -lcrypto -lp11"

        #copy result files to related dir
        mkdir -p ${S}/examples/${plat}

        for bin in ${PKCS_DEMO_BINS}; do
            cp ${S}/examples/${bin}/${bin} ${S}/examples/${plat}
        done

    done
}

do_install() {

    install -d ${D}${libdir}
    install -m 0755 ${S}/libpkcs-hse.so.1.0 ${D}${libdir}/libpkcs-hse.so.1.0
    install -m 0755 ${S}/libhse.so.1.0 ${D}${libdir}/libhse.so.1.0
    ln -s libhse.so.1.0 ${D}${libdir}/libhse.so.1

    install -d ${D}${includedir}
    install -m 0644 ${S}/libhse/*.h ${D}${includedir}
    install -m 0644 ${S}/libpkcs/*.h ${D}${includedir}

    plats="s32g2 s32g3"
    for plat in $plats; do

        install -d ${D}${bindir}/${plat}/
        for bin in ${PKCS_DEMO_BINS}; do
            install -m 0755 ${S}/examples/${plat}/${bin} ${D}${bindir}/${plat}/
        done

    done
}

pkg_postinst_ontarget:${PN}() {

if grep -q "s32g3" /sys/firmware/devicetree/base/compatible ; then
    plat="s32g3"
else
    plat="s32g2"
fi

for bin in ${PKCS_DEMO_BINS}; do
    if [ -f "/usr/bin/${bin}" ]; then
        continue
    fi

    cp /usr/bin/${plat}/${bin} /usr/bin/${bin}
done

# remove the unneeded directories
rm -rf /usr/bin/s32g2
rm -rf /usr/bin/s32g3

}

PACKAGES =+ "${PN}-examples "
FILES:${PN}-examples += "${bindir}"
COMPATIBLE_MACHINE = "^$"
COMPATIBLE_MACHINE:nxp-s32g = "nxp-s32g"
