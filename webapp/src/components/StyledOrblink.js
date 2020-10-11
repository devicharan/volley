import { NavLink } from 'react-router-dom';
import styled from 'styled-components';
import defaultTheme from "@kiwicom/orbit-components/lib/defaultTheme";


export const StyledOrbLink = styled(NavLink)`
    text-decoration: none;
    cursor: pointer;
  display: inline-flex;
   color: inherit;
  align-items: center;
  height: 44px;
    font-family: ${({ theme }) => theme.orbit.fontFamily};
  font-weight: ${({ theme }) => theme.orbit.fontWeightLinks};
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  transition: color ${({ theme }) => theme.orbit.durationFast} ease-in-out;
   :hover,
  :active,
  :focus {
    outline: none;
    text-decoration: none;
    color: ${({ theme }) => theme.orbit.paletteProductNormalHover};
  }
`;


StyledOrbLink.defaultProps = {
    theme: defaultTheme
};