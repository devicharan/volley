import React from "react";
import NavigationBar from "@kiwicom/orbit-components/lib/NavigationBar";
import LinkList from "@kiwicom/orbit-components/lib/LinkList";
import TextLink from "@kiwicom/orbit-components/lib/TextLink";
import Stack from "@kiwicom/orbit-components/lib/Stack";
import ButtonLink from "@kiwicom/orbit-components/lib/ButtonLink";
import {FullScreen} from "../components/Fullscreen";
import LayoutColumn from "@kiwicom/orbit-components/lib/Layout/LayoutColumn";
import Layout from "@kiwicom/orbit-components/lib/Layout";
import {AfterNavbar} from "../components/AfterNavbar";
import {StyledOrbLink} from "../components/StyledOrblink";
import SocialButton from "@kiwicom/orbit-components/lib/SocialButton";


export default function Home({theme}) {
    return (
        <FullScreen>
            <NavigationBar
                dataTest="test"
                onHide={function(){}}
                onMenuOpen={function(){}}
                onShow={function(){}}
            >
                <Stack
                    align="center"
                    flex
                    justify="between"
                    spacing="none"
                >
                    <LinkList direction="row">
                        <StyledOrbLink type="secondary" to={'/'} href={'/'}>
                            Home
                        </StyledOrbLink>
                        <StyledOrbLink type="secondary" to={'/'} href={'/'}>
                            Vertx
                        </StyledOrbLink>
                        <StyledOrbLink type="secondary" to={'/'} href={'/'}>
                            React
                        </StyledOrbLink>
                        <StyledOrbLink type="secondary" to={'/'} href={'/'}>
                            Ngnix
                        </StyledOrbLink>

                    </LinkList>
                    <Stack
                        direction="row"
                        justify="end"
                        shrink
                        spacing="tight"
                    >
                        <StyledOrbLink   to={'/signin'} href={'/signin'} >
                            Signin
                        </StyledOrbLink>
                        <ButtonLink type="secondary">
                            Account
                        </ButtonLink>
                    </Stack>
                </Stack>
            </NavigationBar>
            <AfterNavbar>
                <Layout type="Search">
                    <LayoutColumn>
                    </LayoutColumn>
                    <LayoutColumn>
                        <SocialButton

                            type="github"
                            href={`/api/githubauth`}
                        >

                            Sign in with Github
                        </SocialButton>
                    </LayoutColumn>
                    <LayoutColumn>
                    </LayoutColumn>
                </Layout>
            </AfterNavbar>
        </FullScreen>
    );
}